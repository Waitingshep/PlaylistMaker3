package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track

interface IGetSearchHistoryUseCase {
    operator fun invoke(): List<Track>
}