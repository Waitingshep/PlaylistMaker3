package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track

interface AddTrackToHistoryUseCase {
    operator fun invoke(track: Track)
}