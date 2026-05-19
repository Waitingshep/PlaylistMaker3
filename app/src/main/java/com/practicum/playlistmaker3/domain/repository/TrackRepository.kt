package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}