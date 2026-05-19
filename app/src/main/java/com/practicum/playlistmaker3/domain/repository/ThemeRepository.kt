package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.ThemeMode

interface ThemeRepository {
    fun getThemeMode(): ThemeMode
    fun saveThemeMode(mode: ThemeMode)
}