package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import com.practicum.playlistmaker3.search.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCaseImpl(
    private val repository: SearchHistoryRepository,
    private val favoriteRepository: FavoriteRepository
) : GetSearchHistoryUseCase {

    override suspend operator fun invoke(): List<Track> {
        val history = repository.getHistory()
        if (history.isEmpty()) return emptyList()

        val favoriteIds = favoriteRepository.getFavoriteIds()

        return history.map { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
            track
        }
    }
}