package com.practicum.playlistmaker3.settings.domain.usecase

import com.practicum.playlistmaker3.settings.domain.models.ThemeMode

interface SetThemeUseCase {
    operator fun invoke(mode: ThemeMode)
}