package com.practicum.playlistmaker3.player.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track

interface PlayTrackUseCase {
    fun prepare(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}