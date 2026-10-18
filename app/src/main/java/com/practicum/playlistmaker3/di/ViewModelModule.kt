package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.media.ui.FavoritesViewModel
import com.practicum.playlistmaker3.media.ui.PlaylistsViewModel
import com.practicum.playlistmaker3.player.ui.PlayerViewModel
import com.practicum.playlistmaker3.playlist.ui.CreatePlaylistViewModel
import com.practicum.playlistmaker3.playlist.ui.EditPlaylistViewModel
import com.practicum.playlistmaker3.playlist.ui.PlaylistViewModel
import com.practicum.playlistmaker3.search.ui.SearchViewModel
import com.practicum.playlistmaker3.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
    viewModel { PlaylistViewModel(get(), get(), get(), get()) }
    viewModel { EditPlaylistViewModel(get()) }
}