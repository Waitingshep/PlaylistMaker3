package com.practicum.playlistmaker3.player.ui

import com.practicum.playlistmaker3.search.domain.models.Track

sealed class PlayerState {
    data class Content(
        val track: Track,
        val isFavorite: Boolean
    ) : PlayerState()

    data class Playing(
        val track: Track,
        val currentPosition: Int,
        val isFavorite: Boolean
    ) : PlayerState()

    data class Paused(
        val track: Track,
        val currentPosition: Int,
        val isFavorite: Boolean
    ) : PlayerState()
}