package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository

class AddTrackToFavoriteUseCase(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(track: Track) {
        repository.addToFavorite(track)
    }
}