package com.practicum.playlistmaker3.search.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.practicum.playlistmaker3.playlist.data.db.PlaylistDao
import com.practicum.playlistmaker3.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker3.playlist.data.db.PlaylistTrackDao
import com.practicum.playlistmaker3.playlist.data.db.PlaylistTrackEntity

@Database(
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playlist_maker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}