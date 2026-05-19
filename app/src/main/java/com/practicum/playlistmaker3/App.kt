package com.practicum.playlistmaker3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.presentation.common.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        val getThemeUseCase = Creator.provideGetThemeUseCase()
        val themeMode = getThemeUseCase()
        applyTheme(themeMode == ThemeMode.DARK)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        applyTheme(darkThemeEnabled)
        val setThemeUseCase = Creator.provideSetThemeUseCase()
        val mode = if (darkThemeEnabled) ThemeMode.DARK else ThemeMode.LIGHT
        setThemeUseCase(mode)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}