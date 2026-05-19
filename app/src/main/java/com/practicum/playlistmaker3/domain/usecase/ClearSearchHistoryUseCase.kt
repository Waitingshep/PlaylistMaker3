package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.repository.ISearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val repository: ISearchHistoryRepository
) : IClearSearchHistoryUseCase {
    override operator fun invoke() = repository.clearHistory()
}