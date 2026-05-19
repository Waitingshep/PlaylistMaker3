package com.practicum.playlistmaker3.presentation.search

import com.practicum.playlistmaker3.domain.models.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}