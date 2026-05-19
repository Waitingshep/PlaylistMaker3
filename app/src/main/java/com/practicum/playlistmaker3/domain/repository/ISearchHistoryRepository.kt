package com.practicum.playlistmaker3.domain.repository

import com.practicum.playlistmaker3.domain.models.Track

interface ISearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}