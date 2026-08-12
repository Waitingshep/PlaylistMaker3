package com.practicum.playlistmaker3.search.domain.repository

import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}