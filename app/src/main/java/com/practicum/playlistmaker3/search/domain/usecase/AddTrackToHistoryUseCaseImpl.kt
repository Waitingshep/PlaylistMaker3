package com.practicum.playlistmaker3.search.domain.usecase

import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.SearchHistoryRepository

class AddTrackToHistoryUseCaseImpl(
    private val repository: SearchHistoryRepository
) : AddTrackToHistoryUseCase {
    override operator fun invoke(track: Track) = repository.addTrack(track)
}