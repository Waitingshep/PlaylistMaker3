package com.practicum.playlistmaker3.search.domain.repository

import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(track: Track)
    fun getFavorites(): Flow<List<Track>>
    suspend fun getFavoriteIds(): List<Long>
}