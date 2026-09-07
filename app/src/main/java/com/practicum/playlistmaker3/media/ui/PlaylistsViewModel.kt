package com.practicum.playlistmaker3.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistsUseCase
import kotlinx.coroutines.launch

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}

class PlaylistsViewModel(
    private val getPlaylistsUseCase: GetPlaylistsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            getPlaylistsUseCase().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.value = PlaylistsState.Empty
                } else {
                    _state.value = PlaylistsState.Content(playlists)
                }
            }
        }
    }
}