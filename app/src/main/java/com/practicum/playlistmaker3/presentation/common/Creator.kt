package com.practicum.playlistmaker3.presentation.common

import android.content.Context
import com.practicum.playlistmaker3.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker3.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker3.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker3.di.NetworkModule
import com.practicum.playlistmaker3.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker3.domain.repository.ThemeRepository
import com.practicum.playlistmaker3.domain.repository.TrackRepository
import com.practicum.playlistmaker3.domain.usecase.*

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(NetworkModule.itunesApiService)
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        val sharedPrefs = appContext.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    private fun getThemeRepository(): ThemeRepository {
        val sharedPrefs = appContext.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCaseImpl(getTrackRepository())
    }

    fun provideGetSearchHistoryUseCase(): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCaseImpl(getSearchHistoryRepository())
    }

    fun provideAddTrackToHistoryUseCase(): AddTrackToHistoryUseCase {
        return AddTrackToHistoryUseCaseImpl(getSearchHistoryRepository())
    }

    fun provideClearSearchHistoryUseCase(): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCaseImpl(getSearchHistoryRepository())
    }

    fun provideGetThemeUseCase(): GetThemeUseCase {
        return GetThemeUseCaseImpl(getThemeRepository())
    }

    fun provideSetThemeUseCase(): SetThemeUseCase {
        return SetThemeUseCaseImpl(getThemeRepository())
    }
}