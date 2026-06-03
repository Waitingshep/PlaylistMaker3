package com.practicum.playlistmaker3.player.ui

import com.practicum.playlistmaker3.search.domain.models.Track

sealed interface PlayerState {
    object Loading : PlayerState
    data class Content(val track: Track) : PlayerState
    data class Playing(val track: Track, val currentPosition: Int) : PlayerState
    data class Paused(val track: Track, val currentPosition: Int) : PlayerState
    object Error : PlayerState
}