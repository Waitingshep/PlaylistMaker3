package com.practicum.playlistmaker3.settings.domain.usecase

import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import com.practicum.playlistmaker3.settings.domain.repository.ThemeRepository

class GetThemeUseCaseImpl(
    private val repository: ThemeRepository
) : GetThemeUseCase {
    override fun invoke(): ThemeMode = repository.getThemeMode()
}