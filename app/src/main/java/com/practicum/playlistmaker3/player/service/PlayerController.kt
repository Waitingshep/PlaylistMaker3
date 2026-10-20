package com.practicum.playlistmaker3.player.service

import com.practicum.playlistmaker3.player.ui.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    fun play()
    fun pause()
    fun stop()
    fun getState(): StateFlow<PlayerState?>
    fun showNotification()
    fun hideNotification()
}