package com.practicum.playlistmaker3.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker3.presentation.common.Creator

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(Creator.provideSearchTracksUseCase()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}