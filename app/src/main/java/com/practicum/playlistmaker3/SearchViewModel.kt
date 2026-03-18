package com.practicum.playlistmaker3.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.Track
import com.practicum.playlistmaker3.data.repository.TrackRepository
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

    fun searchTracks(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        lastQuery = query
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
            searchTracks(lastQuery)
        }
    }
}