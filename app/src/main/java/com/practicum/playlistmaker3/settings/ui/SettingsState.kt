package com.practicum.playlistmaker3.settings.ui

import com.practicum.playlistmaker3.settings.domain.models.ThemeMode

sealed interface SettingsState {
    object Idle : SettingsState
    data class ThemeLoaded(val mode: ThemeMode) : SettingsState
}