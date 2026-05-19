package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}