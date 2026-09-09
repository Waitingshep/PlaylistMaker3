package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker3.search.domain.models.Track

class GetPlaylistTracksUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(trackIds: List<Long>): List<Track> {
        return repository.getTracksByIds(trackIds)
    }
}