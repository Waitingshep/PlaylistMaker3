package com.practicum.playlistmaker3.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.Track
import com.practicum.playlistmaker3.data.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}

class SearchViewModel(private val repository: TrackRepository) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var lastQuery: String = ""
    private var searchJob: Job? = null

    fun searchDebounce(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        lastQuery = query

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000L) // 2 секунды
            executeSearch(query)
        }
    }

    private fun executeSearch(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            val result = repository.searchTracks(query)

            result.fold(
                onSuccess = { tracks ->
                    if (tracks.isEmpty()) {
                        _searchState.value = SearchState.Empty
                    } else {
                        _searchState.value = SearchState.Content(tracks)
                    }
                },
                onFailure = { exception ->
                    exception.printStackTrace()
                    _searchState.value = SearchState.Error
                }
            )
        }
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            // Отменяем текущий дебаунс и сразу выполняем поиск
            searchJob?.cancel()
            executeSearch(lastQuery)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}