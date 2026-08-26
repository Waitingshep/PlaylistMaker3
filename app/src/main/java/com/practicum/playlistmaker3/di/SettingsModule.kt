package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCaseImpl
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCaseImpl
import org.koin.dsl.module

val settingsModule = module {
    single<GetThemeUseCase> { GetThemeUseCaseImpl(get()) }
    single<SetThemeUseCase> { SetThemeUseCaseImpl(get()) }
}