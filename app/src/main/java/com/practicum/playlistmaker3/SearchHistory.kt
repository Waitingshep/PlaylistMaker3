package com.practicum.playlistmaker3

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    // Получить историю поиска
    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        if (json.isNullOrEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Добавить трек в историю
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаляем трек, если он уже есть в истории
        history.removeAll { it.trackId == track.trackId }

        // Добавляем новый трек в начало списка
        history.add(0, track)

        // Оставляем только MAX_HISTORY_SIZE записей
        while (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    // Очистить историю
    fun clearHistory() {
        saveHistory(emptyList())
    }

    // Сохранить историю в SharedPreferences
    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }
}