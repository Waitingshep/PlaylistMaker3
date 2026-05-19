package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCaseImpl(
    private val repository: SearchHistoryRepository
) : ClearSearchHistoryUseCase {
    override operator fun invoke() = repository.clearHistory()
}