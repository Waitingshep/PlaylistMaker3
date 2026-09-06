package com.practicum.playlistmaker3.playlist.domain.repository

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun insertTrackToPlaylist(track: Track)
}