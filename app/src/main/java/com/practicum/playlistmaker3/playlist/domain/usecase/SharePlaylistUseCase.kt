package com.practicum.playlistmaker3.playlist.domain.usecase

import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.search.domain.models.Track

class SharePlaylistUseCase {
    operator fun invoke(playlist: Playlist, tracks: List<Track>): String {
        val sb = StringBuilder()

        // Название плейлиста
        sb.append(playlist.name)

        // Описание (если есть)
        if (!playlist.description.isNullOrEmpty()) {
            sb.append("\n").append(playlist.description)
        }

        // Количество треков
        sb.append("\n").append(tracks.size).append(" треков")

        // Список треков
        if (tracks.isNotEmpty()) {
            sb.append("\n")
            tracks.forEachIndexed { index, track ->
                sb.append("\n${index + 1}. ${track.artistName} - ${track.trackName} (${track.formattedTime})")
            }
        }

        return sb.toString()
    }
}