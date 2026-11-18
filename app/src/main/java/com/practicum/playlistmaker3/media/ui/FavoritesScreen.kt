package com.practicum.playlistmaker3.media.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.ui.Routes
import com.practicum.playlistmaker3.player.ui.PlayerViewModel
import com.practicum.playlistmaker3.search.ui.TrackUi
import com.practicum.playlistmaker3.ui.components.EmptyPlaceholder
import com.practicum.playlistmaker3.ui.components.TrackItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = koinViewModel(),
    playerViewModel: PlayerViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val state by viewModel.state.observeAsState()

    FavoritesScreenContent(
        state = state,
        onTrackClick = { track ->
            playerViewModel.setTrackToPlay(track)
            navController.navigate(Routes.PLAYER)
        }
    )
}

@Composable
fun FavoritesScreenContent(
    state: FavoritesState?,
    onTrackClick: (TrackUi) -> Unit
) {
    when (state) {
        is FavoritesState.Empty -> {
            EmptyPlaceholder(
                text = stringResource(R.string.no_favorites),
                iconRes = R.drawable.ic_nothing_found_120
            )
        }
        is FavoritesState.Content -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(state.tracks) { track ->
                    TrackItem(track = track, onClick = { onTrackClick(track) })
                }
            }
        }
        else -> {}
    }
}