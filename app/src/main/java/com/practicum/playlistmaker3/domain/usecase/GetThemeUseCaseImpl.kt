package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.domain.repository.ThemeRepository

class GetThemeUseCaseImpl(
    private val repository: ThemeRepository
) : GetThemeUseCase {
    override fun invoke(): ThemeMode = repository.getThemeMode()
}