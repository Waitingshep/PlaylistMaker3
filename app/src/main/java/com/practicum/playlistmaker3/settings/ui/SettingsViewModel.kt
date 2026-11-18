package com.practicum.playlistmaker3.settings.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCase

class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    init {
        loadTheme()
    }

    private fun loadTheme() {
        val mode = getThemeUseCase()
        _state.value = SettingsState.ThemeLoaded(mode)
    }

    fun setTheme(mode: ThemeMode) {
        setThemeUseCase(mode)
        AppCompatDelegate.setDefaultNightMode(
            if (mode == ThemeMode.DARK) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        _state.value = SettingsState.ThemeLoaded(mode)
    }
}