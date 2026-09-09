package com.practicum.playlistmaker3.playlist.ui

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.search.domain.models.Track

sealed class PlaylistState {
    object Loading : PlaylistState()
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: Long
    ) : PlaylistState()
    object Error : PlaylistState()
}