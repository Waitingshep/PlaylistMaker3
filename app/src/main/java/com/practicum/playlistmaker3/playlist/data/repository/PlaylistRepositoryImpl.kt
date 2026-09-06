package com.practicum.playlistmaker3.playlist.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker3.playlist.data.db.PlaylistDao
import com.practicum.playlistmaker3.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker3.playlist.data.db.PlaylistTrackDao
import com.practicum.playlistmaker3.playlist.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker3.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = mapToEntity(playlist)
        return playlistDao.insert(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = mapToEntity(playlist)
        playlistDao.update(entity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities ->
                entities.map { mapToDomain(it) }
            }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = playlistDao.getPlaylistById(id)
        return entity?.let { mapToDomain(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        if (playlist.trackIds.contains(track.trackId)) {
            return false
        }

        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        playlistTrackDao.insert(trackEntity)

        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )

        updatePlaylist(updatedPlaylist)
        return true
    }

    override suspend fun insertTrackToPlaylist(track: Track) {
        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        playlistTrackDao.insert(trackEntity)
    }

    private fun mapToEntity(playlist: Playlist): PlaylistEntity {
        val trackIdsJson = gson.toJson(playlist.trackIds)
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = trackIdsJson,
            trackCount = playlist.trackCount
        )
    }

    private fun mapToDomain(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds = gson.fromJson<List<Long>>(entity.trackIds, type) ?: emptyList()
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = trackIds,
            trackCount = entity.trackCount
        )
    }
}