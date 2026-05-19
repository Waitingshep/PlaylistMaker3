package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.SearchHistoryRepository

class AddTrackToHistoryUseCaseImpl(
    private val repository: SearchHistoryRepository
) : AddTrackToHistoryUseCase {
    override operator fun invoke(track: Track) = repository.addTrack(track)
}