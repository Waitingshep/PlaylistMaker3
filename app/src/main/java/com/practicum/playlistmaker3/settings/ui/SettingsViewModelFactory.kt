package com.practicum.playlistmaker3.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker3.creator.Creator

class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                Creator.provideGetThemeUseCase(),
                Creator.provideSetThemeUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}