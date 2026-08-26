package com.practicum.playlistmaker3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker3.di.*
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

class App : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                databaseModule,
                dataModule,
                domainModule,
                viewModelModule,
                settingsModule
            )
        }

        val getThemeUseCase: GetThemeUseCase = get()
        val themeMode = getThemeUseCase()
        applyTheme(themeMode == ThemeMode.DARK)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}