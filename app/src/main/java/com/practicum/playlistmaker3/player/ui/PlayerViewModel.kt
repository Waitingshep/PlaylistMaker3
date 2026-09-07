package com.practicum.playlistmaker3.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.AddTrackToPlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistsUseCase
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToFavoriteUseCase
import com.practicum.playlistmaker3.search.domain.usecase.RemoveTrackFromFavoriteUseCase
import com.practicum.playlistmaker3.search.ui.TrackMapper
import com.practicum.playlistmaker3.search.ui.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class PlaylistAddStatus {
    data class Success(val playlistName: String) : PlaylistAddStatus()
    data class AlreadyExists(val playlistName: String) : PlaylistAddStatus()
}

class PlayerViewModel(
    private val playTrackUseCase: PlayTrackUseCase,
    private val addToFavoriteUseCase: AddTrackToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveTrackFromFavoriteUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlayerState>()
    val state: LiveData<PlayerState> = _state

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _playlistAddStatus = MutableLiveData<PlaylistAddStatus?>()
    val playlistAddStatus: LiveData<PlaylistAddStatus?> = _playlistAddStatus

    private val _showPlaylistBottomSheet = MutableLiveData<Boolean>(false)
    val showPlaylistBottomSheet: LiveData<Boolean> = _showPlaylistBottomSheet

    private var updateJob: Job? = null
    private var currentTrack: Track? = null
    private var isPrepared: Boolean = false
    private var currentPosition: Int = 0
    private var isFavorite: Boolean = false

    fun loadTrack(trackUi: TrackUi) {
        viewModelScope.launch {
            val track = TrackMapper.mapToDomain(trackUi)
            currentTrack = track
            isPrepared = false
            currentPosition = 0

            val favoriteIds = favoriteRepository.getFavoriteIds()
            isFavorite = favoriteIds.contains(track.trackId)
            track.isFavorite = isFavorite

            _state.value = PlayerState.Content(track, isFavorite)

            playTrackUseCase.release()
            playTrackUseCase.prepare(track,
                onPrepared = {
                    isPrepared = true
                    _state.value = PlayerState.Content(track, isFavorite)
                },
                onCompletion = {
                    stopUpdating()
                    isPrepared = false
                    currentPosition = 0
                    _state.value = PlayerState.Content(track, isFavorite)
                    playTrackUseCase.stop()
                }
            )
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun showPlaylistBottomSheet() {
        _showPlaylistBottomSheet.value = true
        loadPlaylists()
    }

    fun hidePlaylistBottomSheet() {
        _showPlaylistBottomSheet.value = false
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            if (playlist.trackIds.contains(track.trackId)) {
                _playlistAddStatus.value = PlaylistAddStatus.AlreadyExists(playlist.name)
            } else {
                val success = addTrackToPlaylistUseCase(track, playlist)
                if (success) {
                    _playlistAddStatus.value = PlaylistAddStatus.Success(playlist.name)
                    loadPlaylists()
                }
            }
            _showPlaylistBottomSheet.value = false
            delay(2000)
            _playlistAddStatus.value = null
        }
    }

    fun clearPlaylistAddStatus() {
        _playlistAddStatus.value = null
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                if (isFavorite) {
                    removeFromFavoriteUseCase(track)
                } else {
                    addToFavoriteUseCase(track)
                }
                isFavorite = !isFavorite
                track.isFavorite = isFavorite

                val currentState = _state.value
                _state.value = when (currentState) {
                    is PlayerState.Content -> PlayerState.Content(track, isFavorite)
                    is PlayerState.Playing -> PlayerState.Playing(track, currentState.currentPosition, isFavorite)
                    is PlayerState.Paused -> PlayerState.Paused(track, currentState.currentPosition, isFavorite)
                    else -> PlayerState.Content(track, isFavorite)
                }
            }
        }
    }

    fun play() {
        if (!isPrepared) return
        currentTrack?.let { track ->
            playTrackUseCase.play()
            startUpdating()
            _state.value = PlayerState.Playing(track, playTrackUseCase.getCurrentPosition(), isFavorite)
        }
    }

    fun pause() {
        currentTrack?.let { track ->
            playTrackUseCase.pause()
            stopUpdating()
            val position = playTrackUseCase.getCurrentPosition()
            currentPosition = position
            _state.value = PlayerState.Paused(track, position, isFavorite)
        }
    }

    fun stop() {
        playTrackUseCase.stop()
        stopUpdating()
        isPrepared = false
        currentPosition = 0
        currentTrack?.let { track ->
            _state.value = PlayerState.Content(track, isFavorite)
        }
    }

    private fun startUpdating() {
        stopUpdating()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(300)
                currentTrack?.let { track ->
                    if (playTrackUseCase.isPlaying()) {
                        val position = playTrackUseCase.getCurrentPosition()
                        currentPosition = position
                        _state.value = PlayerState.Playing(track, position, isFavorite)
                    } else {
                        stopUpdating()
                        _state.value = PlayerState.Content(track, isFavorite)
                        playTrackUseCase.stop()
                    }
                }
            }
        }
    }

    private fun stopUpdating() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdating()
        playTrackUseCase.release()
    }
}