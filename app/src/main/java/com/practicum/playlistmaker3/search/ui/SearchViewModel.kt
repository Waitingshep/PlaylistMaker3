package com.practicum.playlistmaker3.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getHistoryUseCase: GetSearchHistoryUseCase,
    private val addToHistoryUseCase: AddTrackToHistoryUseCase,
    private val clearHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> = _uiState

    private var lastQuery: String = ""
    private var searchJob: Job? = null
    private var lastResult: List<Track>? = null

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

    private fun showCachedResult() {
        lastResult?.let { tracks ->
            val uiTracks = tracks.map { mapToUi(it) }
            _uiState.value = if (uiTracks.isEmpty()) SearchUiState.Empty else SearchUiState.Content(uiTracks)
        }
    }

    fun addToHistory(trackUi: TrackUi) {
        val track = mapToDomain(trackUi)
        addToHistoryUseCase(track)
    }

    fun clearHistory() {
        clearHistoryUseCase()
        loadHistory()
    }

    fun restoreState(query: String) {
        if (query.isBlank()) {
            loadHistory()
            return
        }
        if (query == lastQuery && lastResult != null) {
            showCachedResult()
            return
        }
        lastQuery = query
        executeSearch(query)
    }

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            lastQuery = ""
            lastResult = null
            loadHistory()
            return
        }

        if (query != lastQuery) {
            lastResult = null
        }

        _uiState.value = SearchUiState.Loading
        lastQuery = query

        searchJob = viewModelScope.launch {
            delay(2000L)
            if (query == lastQuery && lastResult != null) {
                showCachedResult()
                return@launch
            }
            executeSearch(query)
        }
    }

    private fun executeSearch(query: String) {
        if (query == lastQuery && lastResult != null) {
            showCachedResult()
            return
        }

        viewModelScope.launch {
            searchTracksUseCase(query)
                .onStart {
                    if (_uiState.value !is SearchUiState.Loading) {
                        _uiState.value = SearchUiState.Loading
                    }
                }
                .catch { e ->
                    e.printStackTrace()
                    _uiState.value = SearchUiState.Error
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { tracks ->
                            lastResult = tracks
                            val uiTracks = tracks.map { mapToUi(it) }
                            _uiState.value = if (uiTracks.isEmpty()) SearchUiState.Empty else SearchUiState.Content(uiTracks)
                        },
                        onFailure = {
                            _uiState.value = SearchUiState.Error
                        }
                    )
                }
        }
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            searchJob?.cancel()
            executeSearch(lastQuery)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}