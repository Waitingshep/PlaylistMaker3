package com.practicum.playlistmaker3.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.creator.Creator
import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val searchTracksUseCase = Creator.provideSearchTracksUseCase()
    private val getHistoryUseCase = Creator.provideGetSearchHistoryUseCase()
    private val addToHistoryUseCase = Creator.provideAddTrackToHistoryUseCase()
    private val clearHistoryUseCase = Creator.provideClearSearchHistoryUseCase()

    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> = _uiState

    private var lastQuery: String = ""
    private var searchJob: Job? = null

    private fun mapToUi(track: Track): TrackUi {
        return TrackUi(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private fun mapToDomain(trackUi: TrackUi): Track {
        return Track(
            trackId = trackUi.trackId,
            trackName = trackUi.trackName,
            artistName = trackUi.artistName,
            trackTimeMillis = trackUi.trackTimeMillis,
            artworkUrl100 = trackUi.artworkUrl100,
            collectionName = trackUi.collectionName,
            releaseDate = trackUi.releaseDate,
            primaryGenreName = trackUi.primaryGenreName,
            country = trackUi.country,
            previewUrl = trackUi.previewUrl
        )
    }

    fun loadHistory() {
        val history = getHistoryUseCase()
        val uiHistory = history.map { mapToUi(it) }
        _uiState.value = SearchUiState.History(uiHistory)
    }

    fun addToHistory(trackUi: TrackUi) {
        val track = mapToDomain(trackUi)
        addToHistoryUseCase(track)
        loadHistory()
    }

    fun clearHistory() {
        clearHistoryUseCase()
        loadHistory()
    }

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            loadHistory()
            return
        }
        lastQuery = query
        searchJob = viewModelScope.launch {
            delay(2000L)
            executeSearch(query)
        }
    }

    private suspend fun executeSearch(query: String) {
        if (query.isBlank()) {
            loadHistory()
            return
        }
        _uiState.value = SearchUiState.Loading
        val result = searchTracksUseCase(query)
        result.fold(
            onSuccess = { tracks ->
                val uiTracks = tracks.map { mapToUi(it) }
                _uiState.value = if (uiTracks.isEmpty()) SearchUiState.Empty else SearchUiState.Content(uiTracks)
            },
            onFailure = {
                _uiState.value = SearchUiState.Error
            }
        )
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            searchJob?.cancel()
            viewModelScope.launch {
                executeSearch(lastQuery)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}