package com.practicum.playlistmaker3.domain.models

import android.os.Parcelable
import com.practicum.playlistmaker3.data.dto.ItunesTrack
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null
) : Parcelable {
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
        fun fromItunesTrack(itunesTrack: ItunesTrack): Track {
            return Track(
                trackId = itunesTrack.trackId ?: 0,
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