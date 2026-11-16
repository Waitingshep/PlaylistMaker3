package com.practicum.playlistmaker3.playlist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditPlaylistScreen(
    navController: NavController,
    playlistId: Long,
    viewModel: EditPlaylistViewModel = koinViewModel()
) {
    LaunchedEffect(playlistId) {
        if (playlistId > 0) {
            viewModel.loadPlaylistForEdit(playlistId)
        }
    }

    val state by viewModel.state.observeAsState()
    val isLoaded = state?.let { it.playlistId == playlistId && it.name.isNotEmpty() } ?: false

    if (isLoaded) {
        CreatePlaylistScreen(navController, viewModel)
    } else {
        CreatePlaylistScreen(navController, viewModel)
    }
}