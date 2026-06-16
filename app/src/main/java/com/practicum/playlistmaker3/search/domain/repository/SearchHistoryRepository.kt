package com.practicum.playlistmaker3.search.domain.repository

import com.practicum.playlistmaker3.search.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}