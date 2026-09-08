package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository

class DeleteTrackFromPlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(trackId: Long, playlist: Playlist): Boolean {
        return repository.deleteTrackFromPlaylist(trackId, playlist)
    }
}