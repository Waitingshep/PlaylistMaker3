package com.practicum.playlistmaker3.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker3.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker3.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker3.playlist.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker3.playlist.domain.repository.PlaylistRepository
import com.practicum.playlistmaker3.search.data.db.AppDatabase
import com.practicum.playlistmaker3.search.data.repository.FavoriteRepositoryImpl
import com.practicum.playlistmaker3.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker3.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import com.practicum.playlistmaker3.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import com.practicum.playlistmaker3.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker3.settings.domain.repository.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }

    single<PlayerRepository> { PlayerRepositoryImpl() }

    single<Gson> { Gson() }

    single<SearchHistoryRepository> {
        val sharedPrefs = androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
        SearchHistoryRepositoryImpl(sharedPrefs, get())
    }

    single<ThemeRepository> {
        val sharedPrefs = androidContext().getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
        ThemeRepositoryImpl(sharedPrefs)
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())  // playlistDao, playlistTrackDao, Gson
    }
}