package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist): Long {
        return repository.createPlaylist(playlist)
    }
}