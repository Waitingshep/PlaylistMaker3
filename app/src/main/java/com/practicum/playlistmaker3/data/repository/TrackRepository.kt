package com.practicum.playlistmaker3.data.repository

import com.practicum.playlistmaker3.data.network.ItunesApiService
import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.ITrackRepository
import retrofit2.HttpException
import java.io.IOException

class TrackRepository(
    private val apiService: ItunesApiService
) : ITrackRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())
            val response = apiService.searchTracks(query)
            if (response.resultCount > 0) {
                val tracks = response.results.mapNotNull { itunesTrack ->
                    if (!itunesTrack.trackName.isNullOrBlank() || !itunesTrack.artistName.isNullOrBlank()) {
                        Track.fromItunesTrack(itunesTrack)
                    } else null
                }
                Result.success(tracks)
            } else {
                Result.success(emptyList())
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}