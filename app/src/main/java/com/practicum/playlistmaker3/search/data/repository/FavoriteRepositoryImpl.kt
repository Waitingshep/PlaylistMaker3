package com.practicum.playlistmaker3.search.data.repository

import com.practicum.playlistmaker3.search.data.db.AppDatabase
import com.practicum.playlistmaker3.search.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker3.search.domain.models.Track
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val database: AppDatabase
) : FavoriteRepository {

    private fun mapToEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun mapToDomain(entity: FavoriteTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }

    override suspend fun addToFavorite(track: Track) {
        database.favoriteTrackDao().insert(mapToEntity(track))
    }

    override suspend fun removeFromFavorite(track: Track) {
        database.favoriteTrackDao().delete(mapToEntity(track))
    }

    override fun getFavorites(): Flow<List<Track>> {
        return database.favoriteTrackDao().getAllFavorites()
            .map { entities ->
                entities.map { mapToDomain(it) }
            }
    }

    override suspend fun getFavoriteIds(): List<Long> {
        return database.favoriteTrackDao().getAllFavoriteIds()
    }
}