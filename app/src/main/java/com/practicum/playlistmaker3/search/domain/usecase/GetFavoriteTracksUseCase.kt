package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteTracksUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Track>> = repository.getFavorites()
}