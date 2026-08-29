package com.practicum.playlistmaker3.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.search.domain.usecase.GetFavoriteTracksUseCase
import com.practicum.playlistmaker3.search.ui.TrackMapper
import com.practicum.playlistmaker3.search.ui.TrackUi
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Content(val tracks: List<TrackUi>) : FavoritesState()
}

class FavoritesViewModel(
    private val getFavoriteTracksUseCase: GetFavoriteTracksUseCase
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            getFavoriteTracksUseCase().collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.value = FavoritesState.Empty
                } else {
                    val uiTracks = tracks.map { track ->
                        track.isFavorite = true
                        TrackMapper.mapToUi(track)
                    }
                    _state.value = FavoritesState.Content(uiTracks)
                }
            }
        }
    }
}