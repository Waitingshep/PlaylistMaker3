package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track

interface SearchTracksUseCase {
    suspend operator fun invoke(query: String): Result<List<Track>>
}