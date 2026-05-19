package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode

interface SetThemeUseCase {
    operator fun invoke(mode: ThemeMode)
}