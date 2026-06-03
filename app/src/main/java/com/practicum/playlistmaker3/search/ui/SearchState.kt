package com.practicum.playlistmaker3.search.ui

sealed interface SearchUiState {
    object Loading : SearchUiState
    data class Content(val tracks: List<TrackUi>) : SearchUiState
    data class History(val tracks: List<TrackUi>) : SearchUiState
    object Empty : SearchUiState
    object Error : SearchUiState
}