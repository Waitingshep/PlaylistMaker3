package com.practicum.playlistmaker3.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.ui.TrackMapper
import com.practicum.playlistmaker3.search.ui.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playTrackUseCase: PlayTrackUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlayerState>()
    val state: LiveData<PlayerState> = _state

    private var updateJob: Job? = null
    private var currentTrack: Track? = null
    private var isPrepared: Boolean = false

    fun loadTrack(trackUi: TrackUi) {
        val track = TrackMapper.mapToDomain(trackUi)
        currentTrack = track
        isPrepared = false
        _state.value = PlayerState.Content(track)
        playTrackUseCase.release()
        playTrackUseCase.prepare(track,
            onPrepared = {
                isPrepared = true
                _state.value = PlayerState.Content(track)
            },
            onCompletion = {
                stopUpdating()
                isPrepared = false
                _state.value = PlayerState.Content(track)
                playTrackUseCase.stop()
                // При завершении время сбрасывается в UI через Content
            }
        )
    }

    fun play() {
        if (!isPrepared) return
        currentTrack?.let { track ->
            playTrackUseCase.play()
            startUpdating()
            _state.value = PlayerState.Playing(track, playTrackUseCase.getCurrentPosition())
        }
    }

    fun pause() {
        currentTrack?.let { track ->
            playTrackUseCase.pause()
            stopUpdating()
            _state.value = PlayerState.Paused(track, playTrackUseCase.getCurrentPosition())
        }
    }

    fun stop() {
        playTrackUseCase.stop()
        stopUpdating()
        isPrepared = false
        currentTrack?.let { track ->
            _state.value = PlayerState.Content(track)
        }
    }

    private fun startUpdating() {
        stopUpdating()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(300)
                currentTrack?.let { track ->
                    if (playTrackUseCase.isPlaying()) {
                        _state.value = PlayerState.Playing(track, playTrackUseCase.getCurrentPosition())
                    } else {
                        stopUpdating()
                        _state.value = PlayerState.Content(track)
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