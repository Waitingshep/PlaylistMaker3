package com.practicum.playlistmaker3.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.creator.Creator
import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    private val playTrackUseCase = Creator.providePlayTrackUseCase()

    private val _state = MutableLiveData<PlayerState>()
    val state: LiveData<PlayerState> = _state

    private var updateJob: Job? = null
    private var currentTrack: Track? = null

    fun loadTrack(track: Track) {
        currentTrack = track
        _state.value = PlayerState.Content(track)
        playTrackUseCase.release()
        playTrackUseCase.prepare(track,
            onPrepared = {
                _state.value = PlayerState.Content(track)
            },
            onCompletion = {
                stopUpdating()
                _state.value = PlayerState.Content(track)
                playTrackUseCase.stop()
            }
        )
    }

    fun play() {
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
        currentTrack?.let { track ->
            _state.value = PlayerState.Content(track)
        }
    }

    private fun startUpdating() {
        stopUpdating()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(500)
                currentTrack?.let { track ->
                    if (playTrackUseCase.isPlaying()) {
                        _state.value = PlayerState.Playing(track, playTrackUseCase.getCurrentPosition())
                    } else {
                        stopUpdating()
                        _state.value = PlayerState.Content(track)
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