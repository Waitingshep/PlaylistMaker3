package com.practicum.playlistmaker3.presentation.common

import android.content.Context
import com.practicum.playlistmaker3.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker3.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker3.data.repository.TrackRepository
import com.practicum.playlistmaker3.di.NetworkModule
import com.practicum.playlistmaker3.domain.repository.ISearchHistoryRepository
import com.practicum.playlistmaker3.domain.repository.IThemeRepository
import com.practicum.playlistmaker3.domain.repository.ITrackRepository
import com.practicum.playlistmaker3.domain.usecase.*

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    private fun getTrackRepository(): ITrackRepository {
        return TrackRepository(NetworkModule.itunesApiService)
    }

    private fun getSearchHistoryRepository(): ISearchHistoryRepository {
        val sharedPrefs = appContext.getSharedPreferences("search_history", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    private fun getThemeRepository(): IThemeRepository {
        val sharedPrefs = appContext.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideSearchTracksUseCase(): ISearchTracksUseCase {
        return SearchTracksUseCase(getTrackRepository())
    }

    fun provideGetSearchHistoryUseCase(): IGetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(getSearchHistoryRepository())
    }

    fun provideAddTrackToHistoryUseCase(): IAddTrackToHistoryUseCase {
        return AddTrackToHistoryUseCase(getSearchHistoryRepository())
    }

    fun provideClearSearchHistoryUseCase(): IClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(getSearchHistoryRepository())
    }

    fun provideGetThemeUseCase(): IGetThemeUseCase {
        return GetThemeUseCase(getThemeRepository())
    }

    fun provideSetThemeUseCase(): ISetThemeUseCase {
        return SetThemeUseCase(getThemeRepository())
    }
}