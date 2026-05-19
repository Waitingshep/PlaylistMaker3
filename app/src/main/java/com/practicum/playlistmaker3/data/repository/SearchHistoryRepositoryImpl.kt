package com.practicum.playlistmaker3.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker3.domain.models.Track
import com.practicum.playlistmaker3.domain.repository.ISearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ISearchHistoryRepository {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val maxHistorySize = 10

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        while (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}