package com.practicum.playlistmaker3.search.ui

import com.practicum.playlistmaker3.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    object Empty : SearchState
    object Error : SearchState
}