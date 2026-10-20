package com.practicum.playlistmaker3.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.player.service.PlayerController
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class PlaylistAddStatus {
    data class Success(val playlistName: String) : PlaylistAddStatus()
    data class AlreadyExists(val playlistName: String) : PlaylistAddStatus()
}

class PlayerViewModel(
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

    private var playerController: PlayerController? = null
    private var stateJob: Job? = null
    private var currentTrack: Track? = null
    private var isFavorite: Boolean = false
    private var isUiResumed: Boolean = false

    fun onServiceConnected(controller: PlayerController) {
        playerController = controller
        observeServiceState()
    }

    private fun observeServiceState() {
        stateJob?.cancel()
        stateJob = viewModelScope.launch {
            playerController?.getState()?.collectLatest { state ->
                state?.let {
                    _state.value = it
                    if (it is PlayerState.Playing) {
                        currentTrack = it.track
                        isFavorite = it.isFavorite
                    }
                }
            }
        }
    }

    fun loadTrack(trackUi: TrackUi) {
        viewModelScope.launch {
            val track = TrackMapper.mapToDomain(trackUi)
            currentTrack = track
            val favoriteIds = favoriteRepository.getFavoriteIds()
            isFavorite = favoriteIds.contains(track.trackId)
            track.isFavorite = isFavorite
            _state.value = PlayerState.Content(track, isFavorite)
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
        playerController?.play()
        if (!isUiResumed) {
            playerController?.showNotification()
        }
    }

    fun pause() {
        playerController?.pause()
        playerController?.hideNotification()
    }

    fun stop() {
        playerController?.stop()
        playerController?.hideNotification()
    }

    fun onUiResumed() {
        isUiResumed = true
        playerController?.hideNotification()
    }

    fun onUiPaused() {
        isUiResumed = false
        val state = _state.value
        if (state is PlayerState.Playing) {
            playerController?.showNotification()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stateJob?.cancel()
    }
}