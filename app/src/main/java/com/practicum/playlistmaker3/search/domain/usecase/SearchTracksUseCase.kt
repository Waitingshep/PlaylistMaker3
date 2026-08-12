package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksUseCase {
    operator fun invoke(query: String): Flow<Result<List<Track>>>
}