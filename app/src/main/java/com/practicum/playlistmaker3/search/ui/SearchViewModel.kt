package com.practicum.playlistmaker3.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.search.ui.SearchState
import com.practicum.playlistmaker3.search.domain.usecase.SearchTracksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var lastQuery: String = ""
    private var searchJob: Job? = null

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
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
            _searchState.value = SearchState.Empty
            return
        }
        _searchState.value = SearchState.Loading
        val result = searchTracksUseCase(query)
        result.fold(
            onSuccess = { tracks ->
                _searchState.value = if (tracks.isEmpty()) SearchState.Empty else SearchState.Content(tracks)
            },
            onFailure = {
                _searchState.value = SearchState.Error
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