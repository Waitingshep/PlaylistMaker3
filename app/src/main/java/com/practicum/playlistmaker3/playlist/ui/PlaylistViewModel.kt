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
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
    private val deleteTrackFromPlaylistUseCase: DeleteTrackFromPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = _state

    private val _shareText = MutableLiveData<String?>()
    val shareText: LiveData<String?> = _shareText

    private val _showShareDialog = MutableLiveData<Boolean>(false)
    val showShareDialog: LiveData<Boolean> = _showShareDialog

    private val _showDeleteConfirmation = MutableLiveData<Boolean>(false)
    val showDeleteConfirmation: LiveData<Boolean> = _showDeleteConfirmation

    private val _playlistDeleted = MutableLiveData<Boolean>(false)
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    private val _showEmptyShareToast = MutableLiveData<Boolean>(false)
    val showEmptyShareToast: LiveData<Boolean> = _showEmptyShareToast

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
            _showEmptyShareToast.value = true
        } else {
            _showShareDialog.value = true
        }
    }

    fun sharePlaylist() {
        val playlist = currentPlaylist ?: return
        val tracks = currentTracks
        if (tracks.isEmpty()) return

        val shareUseCase = SharePlaylistUseCase()
        val text = shareUseCase(playlist, tracks)
        _shareText.value = text
        _showShareDialog.value = false
    }

    fun deletePlaylist() {
        _showDeleteConfirmation.value = true
    }

    fun confirmDeletePlaylist() {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            deletePlaylistUseCase(playlist)
            _playlistDeleted.value = true
            _showDeleteConfirmation.value = false
        }
    }

    fun cancelDeletePlaylist() {
        _showDeleteConfirmation.value = false
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            val success = deleteTrackFromPlaylistUseCase(trackId, playlist)
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

    fun resetShareDialog() {
        _showShareDialog.value = false
    }

    fun resetEmptyShareToast() {
        _showEmptyShareToast.value = false
    }

    fun resetPlaylistDeleted() {
        _playlistDeleted.value = false
    }

    fun navigateToPlayer(track: Track) {

    }
}