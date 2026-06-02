package com.practicum.playlistmaker3.creator

import android.content.Context
import com.practicum.playlistmaker3.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker3.settings.domain.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker3.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker3.NetworkModule
import com.practicum.playlistmaker3.settings.domain.repository.ThemeRepository
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCaseImpl
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCaseImpl
import com.practicum.playlistmaker3.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker3.search.domain.repository.TrackRepository
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.ClearSearchHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.ClearSearchHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.GetSearchHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.GetSearchHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.SearchTracksUseCase
import com.practicum.playlistmaker3.search.domain.usecase.SearchTracksUseCaseImpl

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