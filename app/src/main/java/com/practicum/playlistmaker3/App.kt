package com.practicum.playlistmaker3

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val THEME_PREFERENCES = "theme_preferences"
        const val DARK_THEME_KEY = "dark_theme"
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        // Загружаем сохраненную тему из SharedPreferences
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)

        // Применяем сохраненную тему
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        // Сохраняем выбор темы
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()
    }
}