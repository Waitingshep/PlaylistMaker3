package com.practicum.playlistmaker3.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker3.creator.Creator
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val getThemeUseCase = Creator.provideGetThemeUseCase()
    private val setThemeUseCase = Creator.provideSetThemeUseCase()

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
        viewModelScope.launch {
            setThemeUseCase(mode)
            _state.value = SettingsState.ThemeLoaded(mode)
        }
    }
}