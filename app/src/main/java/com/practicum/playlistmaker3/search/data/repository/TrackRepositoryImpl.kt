package com.practicum.playlistmaker3.search.data.repository

import com.practicum.playlistmaker3.search.data.network.ItunesApiService
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class TrackRepositoryImpl(
    private val apiService: ItunesApiService
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            if (query.isBlank()) {
                emit(Result.success(emptyList()))
                return@flow
            }
            val response = apiService.searchTracks(query)
            if (response.resultCount > 0) {
                val tracks = response.results.mapNotNull { itunesTrack ->
                    if (!itunesTrack.trackName.isNullOrBlank() || !itunesTrack.artistName.isNullOrBlank()) {
                        Track.fromItunesTrack(itunesTrack)
                    } else null
                }
                emit(Result.success(tracks))
            } else {
                emit(Result.success(emptyList()))
            }
        } catch (e: IOException) {
            emit(Result.failure(e))
        } catch (e: HttpException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}