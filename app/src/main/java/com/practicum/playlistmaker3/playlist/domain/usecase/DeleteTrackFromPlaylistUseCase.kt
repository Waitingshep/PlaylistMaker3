package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository

class DeleteTrackFromPlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(trackId: Long, playlistId: Long): Boolean {
        return repository.deleteTrackFromPlaylist(trackId, playlistId)
    }
}