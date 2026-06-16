package com.practicum.playlistmaker3.search.domain.models

import com.practicum.playlistmaker3.search.data.dto.ItunesTrackDto
import java.text.SimpleDateFormat
import java.util.Locale

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
    val formattedTime: String
        get() = try {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        } catch (e: Exception) {
            "00:00"
        }

    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

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