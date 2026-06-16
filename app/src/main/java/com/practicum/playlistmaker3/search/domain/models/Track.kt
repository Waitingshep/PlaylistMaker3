package com.practicum.playlistmaker3.search.domain.models

import com.practicum.playlistmaker3.search.data.dto.ItunesTrackDto

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null
) {
    val releaseYear: String?
        get() = releaseDate?.take(4)

    companion object {
        fun fromItunesTrack(itunesTrack: ItunesTrackDto): Track {
            return Track(
                trackId = itunesTrack.trackId ?: 0L,
                trackName = itunesTrack.trackName ?: "",
                artistName = itunesTrack.artistName ?: "",
                trackTimeMillis = itunesTrack.trackTimeMillis ?: 0,
                artworkUrl100 = itunesTrack.artworkUrl100 ?: "",
                collectionName = itunesTrack.collectionName,
                releaseDate = itunesTrack.releaseDate,
                primaryGenreName = itunesTrack.primaryGenreName,
                country = itunesTrack.country,
                previewUrl = itunesTrack.previewUrl
            )
        }
    }
}