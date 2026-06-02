package com.practicum.playlistmaker3.settings.domain.repository

import com.practicum.playlistmaker3.settings.domain.models.ThemeMode

interface ThemeRepository {
    fun getThemeMode(): ThemeMode
    fun saveThemeMode(mode: ThemeMode)
}