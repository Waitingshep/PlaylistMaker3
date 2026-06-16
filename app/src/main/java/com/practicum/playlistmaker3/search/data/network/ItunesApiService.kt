package com.practicum.playlistmaker3.search.data.network

import com.practicum.playlistmaker3.search.data.dto.TrackResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") query: String
    ): TrackResponseDto
}