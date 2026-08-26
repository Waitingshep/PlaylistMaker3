package com.practicum.playlistmaker3.search.ui

import com.practicum.playlistmaker3.search.domain.models.Track

object TrackMapper {
    fun mapToUi(track: Track): TrackUi {
        return TrackUi(
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
    }

    fun mapToDomain(trackUi: TrackUi): Track {
        return Track(
            trackId = trackUi.trackId,
            trackName = trackUi.trackName,
            artistName = trackUi.artistName,
            trackTimeMillis = trackUi.trackTimeMillis,
            artworkUrl100 = trackUi.artworkUrl100,
            collectionName = trackUi.collectionName,
            releaseDate = trackUi.releaseDate,
            primaryGenreName = trackUi.primaryGenreName,
            country = trackUi.country,
            previewUrl = trackUi.previewUrl
        )
    }
}