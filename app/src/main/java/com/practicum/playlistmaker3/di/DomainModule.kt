package com.practicum.playlistmaker3.di

import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCase
import com.practicum.playlistmaker3.player.domain.usecase.PlayTrackUseCaseImpl
import com.practicum.playlistmaker3.playlist.domain.usecase.AddTrackToPlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.CreatePlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.DeletePlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.DeleteTrackFromPlaylistUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistByIdUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistsUseCase
import com.practicum.playlistmaker3.playlist.domain.usecase.GetPlaylistTracksUseCase
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToFavoriteUseCase
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.AddTrackToHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.ClearSearchHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.ClearSearchHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.GetFavoriteTracksUseCase
import com.practicum.playlistmaker3.search.domain.usecase.GetSearchHistoryUseCase
import com.practicum.playlistmaker3.search.domain.usecase.GetSearchHistoryUseCaseImpl
import com.practicum.playlistmaker3.search.domain.usecase.RemoveTrackFromFavoriteUseCase
import com.practicum.playlistmaker3.search.domain.usecase.SearchTracksUseCase
import com.practicum.playlistmaker3.search.domain.usecase.SearchTracksUseCaseImpl
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.GetThemeUseCaseImpl
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCase
import com.practicum.playlistmaker3.settings.domain.usecase.SetThemeUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    // Search
    single<SearchTracksUseCase> { SearchTracksUseCaseImpl(get()) }
    single<GetSearchHistoryUseCase> { GetSearchHistoryUseCaseImpl(get(), get()) }
    single<AddTrackToHistoryUseCase> { AddTrackToHistoryUseCaseImpl(get()) }
    single<ClearSearchHistoryUseCase> { ClearSearchHistoryUseCaseImpl(get()) }

    // Favorites
    single { AddTrackToFavoriteUseCase(get()) }
    single { RemoveTrackFromFavoriteUseCase(get()) }
    single { GetFavoriteTracksUseCase(get()) }

    // Settings
    single<GetThemeUseCase> { GetThemeUseCaseImpl(get()) }
    single<SetThemeUseCase> { SetThemeUseCaseImpl(get()) }

    // Player
    single<PlayTrackUseCase> { PlayTrackUseCaseImpl(get()) }

    // Playlist
    single<CreatePlaylistUseCase> { CreatePlaylistUseCase(get()) }
    single<GetPlaylistsUseCase> { GetPlaylistsUseCase(get()) }
    single<AddTrackToPlaylistUseCase> { AddTrackToPlaylistUseCase(get()) }
    single<GetPlaylistByIdUseCase> { GetPlaylistByIdUseCase(get()) }
    single<GetPlaylistTracksUseCase> { GetPlaylistTracksUseCase(get()) }
    single<DeleteTrackFromPlaylistUseCase> { DeleteTrackFromPlaylistUseCase(get()) }
    single<DeletePlaylistUseCase> { DeletePlaylistUseCase(get()) }
}