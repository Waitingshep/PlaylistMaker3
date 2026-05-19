package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track

interface IAddTrackToHistoryUseCase {
    operator fun invoke(track: Track)
}