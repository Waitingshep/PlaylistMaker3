package com.practicum.playlistmaker3.playlist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.usecase.DeletePlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.DeleteTrackFromPlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistByIdUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistTracksUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.SharePlaylistUseCase
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.utils.SingleLiveEvent
import kotlinx.coroutines.launch

sealed class PlaylistEvent {
    data class SharePlaylist(val text: String) : PlaylistEvent()
    object ShowEmptyShareToast : PlaylistEvent()
    object ShowDeleteConfirmation : PlaylistEvent()
    object PlaylistDeleted : PlaylistEvent()
    object NavigateBack : PlaylistEvent()
}

class PlaylistViewModel(
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
    private val deleteTrackFromPlaylistUseCase: DeleteTrackFromPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = _state

    private val _event = SingleLiveEvent<PlaylistEvent>()
    val event: LiveData<PlaylistEvent> = _event

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            _state.value = PlaylistState.Loading
            try {
                val playlist = getPlaylistByIdUseCase(playlistId)
                if (playlist != null) {
                    currentPlaylist = playlist
                    val tracks = getPlaylistTracksUseCase(playlist.trackIds)
                    currentTracks = tracks
                    val totalDuration = tracks.sumOf { it.trackTimeMillis }
                    _state.value = PlaylistState.Content(
                        playlist = playlist,
                        tracks = tracks,
                        totalDuration = totalDuration
                    )
                } else {
                    _state.value = PlaylistState.Error
                }
            } catch (e: Exception) {
                _state.value = PlaylistState.Error
            }
        }
    }

    fun onShareClick() {
        val tracks = currentTracks
        if (tracks.isEmpty()) {
            _event.value = PlaylistEvent.ShowEmptyShareToast
        } else {
            sharePlaylist()
        }
    }

    private fun sharePlaylist() {
        val playlist = currentPlaylist ?: return
        val tracks = currentTracks
        if (tracks.isEmpty()) return

        val shareUseCase = SharePlaylistUseCase()
        val text = shareUseCase(playlist, tracks)
        _event.value = PlaylistEvent.SharePlaylist(text)
    }

    fun deletePlaylist() {
        _event.value = PlaylistEvent.ShowDeleteConfirmation
    }

    fun confirmDeletePlaylist() {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            deletePlaylistUseCase(playlist.id)
            _event.value = PlaylistEvent.PlaylistDeleted
        }
    }

    fun cancelDeletePlaylist() {
        // Обработка отмены - ничего не делаем
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            val success = deleteTrackFromPlaylistUseCase(trackId, playlist.id)
            if (success) {
                val updatedPlaylist = getPlaylistByIdUseCase(playlist.id)
                if (updatedPlaylist != null) {
                    currentPlaylist = updatedPlaylist
                    val tracks = getPlaylistTracksUseCase(updatedPlaylist.trackIds)
                    currentTracks = tracks
                    val totalDuration = tracks.sumOf { it.trackTimeMillis }
                    _state.value = PlaylistState.Content(
                        playlist = updatedPlaylist,
                        tracks = tracks,
                        totalDuration = totalDuration
                    )
                }
            }
        }
    }
}