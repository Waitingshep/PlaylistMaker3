package com.practicum.playlistmaker3.search.ui

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val tracks: List<TrackUi>) : SearchState
    data class History(val tracks: List<TrackUi>) : SearchState
    object Empty : SearchState
    object Error : SearchState
}