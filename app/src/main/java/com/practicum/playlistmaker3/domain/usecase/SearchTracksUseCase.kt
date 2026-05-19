package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track

interface SearchTracksUseCase {
    suspend operator fun invoke(query: String): Result<List<Track>>
}