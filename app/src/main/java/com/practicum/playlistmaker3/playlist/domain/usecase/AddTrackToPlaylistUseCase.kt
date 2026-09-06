package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker3.search.domain.models.Track

class AddTrackToPlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(track: Track, playlist: Playlist): Boolean {
        return repository.addTrackToPlaylist(track, playlist)
    }
}