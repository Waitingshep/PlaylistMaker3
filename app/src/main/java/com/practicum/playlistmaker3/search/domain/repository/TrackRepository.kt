package com.practicum.playlistmaker3.search.domain.repository

import com.practicum.playlistmaker3.search.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}