package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track

interface GetSearchHistoryUseCase {
    operator fun invoke(): List<Track>
}