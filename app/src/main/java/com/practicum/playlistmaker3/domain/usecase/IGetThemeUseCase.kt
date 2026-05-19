package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode

interface IGetThemeUseCase {
    operator fun invoke(): ThemeMode
}