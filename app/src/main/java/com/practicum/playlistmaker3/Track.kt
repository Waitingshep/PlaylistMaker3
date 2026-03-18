package com.practicum.playlistmaker3

import com.practicum.playlistmaker3.data.models.ItunesTrack
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String // Ссылка на изображение обложки
) {
    // Cвойство для отформатированного времени
    val formattedTime: String
        get() = try {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        } catch (e: Exception) {
            "00:00"
        }

    companion object {
        fun fromItunesTrack(itunesTrack: ItunesTrack): Track {
            return Track(
                trackName = itunesTrack.trackName ?: "",
                artistName = itunesTrack.artistName ?: "",
                trackTimeMillis = itunesTrack.trackTimeMillis ?: 0,
                artworkUrl100 = itunesTrack.artworkUrl100 ?: ""
            )
        }
    }
}