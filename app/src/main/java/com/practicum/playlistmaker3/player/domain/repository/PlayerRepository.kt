package com.practicum.playlistmaker3.player.domain.repository

interface PlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}