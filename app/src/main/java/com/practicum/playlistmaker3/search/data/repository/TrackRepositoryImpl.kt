package com.practicum.playlistmaker3.search.data.repository

import com.practicum.playlistmaker3.search.data.network.ItunesApiService
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(
    private val apiService: ItunesApiService
) : TrackRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            if (query.isBlank()) return Result.success(emptyList())
            val response = apiService.searchTracks(query)
            if (response.resultCount > 0) {
                val tracks = response.results.mapNotNull { itunesTrack ->
                    if (!itunesTrack.trackName.isNullOrBlank() || !itunesTrack.artistName.isNullOrBlank()) {
                        Track.Companion.fromItunesTrack(itunesTrack)
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