package com.practicum.playlistmaker3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker3.creator.Creator
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        val getThemeUseCase = Creator.provideGetThemeUseCase()
        val themeMode = getThemeUseCase()
        applyTheme(themeMode == ThemeMode.DARK)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}