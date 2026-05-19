package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.ThemeMode

interface IThemeRepository {
    fun getThemeMode(): ThemeMode
    fun saveThemeMode(mode: ThemeMode)
}