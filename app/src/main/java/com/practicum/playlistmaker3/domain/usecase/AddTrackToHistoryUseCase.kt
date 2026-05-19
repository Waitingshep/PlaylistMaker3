package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.ISearchHistoryRepository

class AddTrackToHistoryUseCase(
    private val repository: ISearchHistoryRepository
) : IAddTrackToHistoryUseCase {
    override operator fun invoke(track: Track) = repository.addTrack(track)
}