package com.practicum.playlistmaker3.playlist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val coverPath: String? = null,
    val trackIds: String = "[]",
    val trackCount: Int = 0
)