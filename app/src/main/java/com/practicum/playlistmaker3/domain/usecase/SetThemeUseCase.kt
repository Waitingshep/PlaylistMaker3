package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.domain.repository.IThemeRepository

class SetThemeUseCase(
    private val repository: IThemeRepository
) : ISetThemeUseCase {
    override fun invoke(mode: ThemeMode) = repository.saveThemeMode(mode)
}