package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track

interface GetSearchHistoryUseCase {
    operator fun invoke(): List<Track>
}