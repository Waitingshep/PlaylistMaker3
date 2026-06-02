package com.practicum.playlistmaker3.settings.domain.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    private val themeKey = "dark_theme"

    override fun getThemeMode(): ThemeMode {
        val isDark = sharedPreferences.getBoolean(themeKey, false)
        return if (isDark) ThemeMode.DARK else ThemeMode.LIGHT
    }

    override fun saveThemeMode(mode: ThemeMode) {
        val isDark = mode == ThemeMode.DARK
        sharedPreferences.edit().putBoolean(themeKey, isDark).apply()
    }
}