package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.domain.repository.ThemeRepository

class SetThemeUseCaseImpl(
    private val repository: ThemeRepository
) : SetThemeUseCase {
    override fun invoke(mode: ThemeMode) = repository.saveThemeMode(mode)
}