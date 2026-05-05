package com.practicum.playlistmaker3

import android.os.Parcelable
import com.practicum.playlistmaker3.data.models.ItunesTrack
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Int, // Уникальный идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека в миллисекундах
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String? = null, // Название альбома
    val releaseDate: String? = null, // Дата релиза
    val primaryGenreName: String? = null, // Жанр
    val country: String? = null // Страна
) : Parcelable {
    // Cвойство для отформатированного времени
    val formattedTime: String
        get() = try {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        } catch (e: Exception) {
            "00:00"
        }

    // Функция для получения качественной обложки для плеера
    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    // Получение года из даты релиза
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
                country = itunesTrack.country
            )
        }
    }
}