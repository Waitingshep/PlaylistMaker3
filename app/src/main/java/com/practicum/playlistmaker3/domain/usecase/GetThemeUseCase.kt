package com.practicum.playlistmaker3.domain.usecase

import com.practicum.playlistmaker3.domain.models.ThemeMode
import com.practicum.playlistmaker3.domain.repository.IThemeRepository

class GetThemeUseCase(
    private val repository: IThemeRepository
) : IGetThemeUseCase {
    override fun invoke(): ThemeMode = repository.getThemeMode()
}