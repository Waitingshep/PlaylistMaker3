package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.*
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCaseImpl
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    // Search
    single<SearchTracksUseCase> { SearchTracksUseCaseImpl(get()) }
    single<GetSearchHistoryUseCase> { GetSearchHistoryUseCaseImpl(get()) }
    single<AddTrackToHistoryUseCase> { AddTrackToHistoryUseCaseImpl(get()) }
    single<ClearSearchHistoryUseCase> { ClearSearchHistoryUseCaseImpl(get()) }

    // Settings
    single<GetThemeUseCase> { GetThemeUseCaseImpl(get()) }
    single<SetThemeUseCase> { SetThemeUseCaseImpl(get()) }

    // Player
    single<PlayTrackUseCase> { PlayTrackUseCaseImpl(get()) }
}