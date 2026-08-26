package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.search.data.repository.FavoriteRepositoryImpl
import com.practicum.playlistmaker3.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker3.search.domain.repository.FavoriteRepository
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get())
    }
}