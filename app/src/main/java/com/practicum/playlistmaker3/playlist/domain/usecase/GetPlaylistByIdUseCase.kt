package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository

class GetPlaylistByIdUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }
}