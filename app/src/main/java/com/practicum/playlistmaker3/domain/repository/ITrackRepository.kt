package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.Track

interface ITrackRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}