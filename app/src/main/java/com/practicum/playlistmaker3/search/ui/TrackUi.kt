package com.practicum.playlistmaker3.search.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class TrackUi(
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
}