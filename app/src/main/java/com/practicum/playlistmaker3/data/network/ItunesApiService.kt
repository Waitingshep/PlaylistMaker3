package com.practicum.playlistmaker3.data.network

import com.practicum.playlistmaker3.data.dto.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") query: String
    ): TrackResponse
}