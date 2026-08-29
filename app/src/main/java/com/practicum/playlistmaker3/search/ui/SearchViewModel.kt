package com.practicum.playlistmaker3.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getHistoryUseCase: GetSearchHistoryUseCase,
    private val addToHistoryUseCase: AddTrackToHistoryUseCase,
    private val clearHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var lastQuery: String = ""
    private var searchJob: Job? = null
    private var lastResult: List<Track>? = null

    private var currentQuery: String = ""
    private var hasSearchResults: Boolean = false

    fun loadHistory() {
        if (!hasSearchResults || currentQuery.isEmpty()) {
            viewModelScope.launch {
                val history = getHistoryUseCase()
                val uiHistory = history.map { TrackMapper.mapToUi(it) }
                _state.value = SearchState.History(uiHistory)
            }
        }
    }

    fun restoreSearchState() {
        if (hasSearchResults && lastResult != null && currentQuery.isNotEmpty()) {
            showCachedResult()
        } else if (currentQuery.isNotEmpty() && lastResult == null) {
            viewModelScope.launch {
                executeSearch(currentQuery)
            }
        } else {
            loadHistory()
        }
    }

    private fun showCachedResult() {
        lastResult?.let { tracks ->
            val uiTracks = tracks.map { TrackMapper.mapToUi(it) }
            _state.value = if (uiTracks.isEmpty()) SearchState.Empty else SearchState.Content(uiTracks)
        }
    }

    fun addToHistory(trackUi: TrackUi) {
        val track = TrackMapper.mapToDomain(trackUi)
        addToHistoryUseCase(track)
    }

    fun clearHistory() {
        clearHistoryUseCase()
        loadHistory()
    }

    fun restoreState(query: String) {
        if (query.isBlank()) {
            currentQuery = ""
            hasSearchResults = false
            loadHistory()
            return
        }

        currentQuery = query

        if (query == lastQuery && lastResult != null) {
            hasSearchResults = true
            showCachedResult()
            return
        }

        lastQuery = query
        viewModelScope.launch {
            executeSearch(query)
        }
    }

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            currentQuery = ""
            hasSearchResults = false
            lastQuery = ""
            lastResult = null
            loadHistory()
            return
        }

        currentQuery = query

        if (query != lastQuery) {
            lastResult = null
        }

        _state.value = SearchState.Loading
        lastQuery = query

        searchJob = viewModelScope.launch {
            delay(2000L)
            if (query == lastQuery && lastResult != null) {
                hasSearchResults = true
                showCachedResult()
                return@launch
            }
            executeSearch(query)
        }
    }

    private suspend fun executeSearch(query: String) {
        if (query == lastQuery && lastResult != null) {
            hasSearchResults = true
            showCachedResult()
            return
        }

        try {
            val result = searchTracksUseCase(query)
            result.fold(
                onSuccess = { tracks ->
                    lastResult = tracks
                    hasSearchResults = true
                    val uiTracks = tracks.map { TrackMapper.mapToUi(it) }
                    _state.value = if (uiTracks.isEmpty()) SearchState.Empty else SearchState.Content(uiTracks)
                },
                onFailure = {
                    hasSearchResults = false
                    _state.value = SearchState.Error
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hasSearchResults = false
            _state.value = SearchState.Error
        }
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