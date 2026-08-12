package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override operator fun invoke(query: String): Flow<Result<List<Track>>> = repository.searchTracks(query)
}