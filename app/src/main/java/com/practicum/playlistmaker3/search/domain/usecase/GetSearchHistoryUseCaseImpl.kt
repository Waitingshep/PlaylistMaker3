package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCaseImpl(
    private val repository: SearchHistoryRepository
) : GetSearchHistoryUseCase {
    override operator fun invoke(): List<Track> = repository.getHistory()
}