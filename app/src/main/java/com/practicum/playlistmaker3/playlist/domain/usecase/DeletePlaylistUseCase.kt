package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository

class DeletePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}