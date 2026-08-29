package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track

interface GetSearchHistoryUseCase {
    suspend operator fun invoke(): List<Track>
}