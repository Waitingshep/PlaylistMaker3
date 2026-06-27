package com.practicum.playlistmaker3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker3.di.dataModule
import com.practicum.playlistmaker3.di.domainModule
import com.practicum.playlistmaker3.di.networkModule
import com.practicum.playlistmaker3.di.viewModelModule
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                dataModule,
                domainModule,
                viewModelModule
            )
        }

        val getThemeUseCase: GetThemeUseCase by inject()
        val themeMode = getThemeUseCase()
        applyTheme(themeMode == ThemeMode.DARK)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}