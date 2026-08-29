package com.practicum.playlistmaker3.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToFavoriteUseCase
import com.practicum.playlistmaker3.search.domain.usecase.RemoveTrackFromFavoriteUseCase
import com.practicum.playlistmaker3.search.ui.TrackMapper
import com.practicum.playlistmaker3.search.ui.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playTrackUseCase: PlayTrackUseCase,
    private val addToFavoriteUseCase: AddTrackToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveTrackFromFavoriteUseCase,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _state = MutableLiveData<PlayerState>()
    val state: LiveData<PlayerState> = _state

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