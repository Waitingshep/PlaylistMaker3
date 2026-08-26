package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override suspend fun invoke(query: String): Result<List<Track>> = repository.searchTracks(query)
}