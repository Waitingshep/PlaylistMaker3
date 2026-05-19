package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.TrackRepository

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override suspend fun invoke(query: String): Result<List<Track>> = repository.searchTracks(query)
}