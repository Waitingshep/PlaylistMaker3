
package com.practicum.playlistmaker3.playlist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    CreatePlaylistScreen(
        navController = navController,
        viewModel = viewModel
    )
}
