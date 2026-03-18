package com.practicum.playlistmaker3.data.repository

import com.practicum.playlistmaker3.Track
import com.practicum.playlistmaker3.data.models.TrackResponse
import com.practicum.playlistmaker3.data.network.ItunesApiService
import retrofit2.HttpException
import java.io.IOException

class TrackRepository(private val apiService: ItunesApiService) {

    suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }

            val response: TrackResponse = apiService.searchTracks(query)

            if (response.resultCount > 0) {
                val tracks = response.results.mapNotNull { itunesTrack ->
                    if (!itunesTrack.trackName.isNullOrBlank() ||
                        !itunesTrack.artistName.isNullOrBlank()) {
                        Track.fromItunesTrack(itunesTrack)
                    } else {
                        null
                    }
                }
                Result.success(tracks)
            } else {
                Result.success(emptyList())
            }
        } catch (e: IOException) {
            // Ошибка сети
            Result.failure(e)
        } catch (e: HttpException) {
            // Ошибка HTTP (код не 200)
            Result.failure(e)
        } catch (e: Exception) {
            // Другие ошибки
            Result.failure(e)
        }
    }
}