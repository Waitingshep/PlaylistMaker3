package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.ISearchHistoryRepository

class GetSearchHistoryUseCase(
    private val repository: ISearchHistoryRepository
) : IGetSearchHistoryUseCase {
    override operator fun invoke(): List<Track> = repository.getHistory()
}