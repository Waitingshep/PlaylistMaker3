package com.practicum.playlistmaker3.media.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.main.ui.Routes
import com.practicum.playlistmaker3.player.ui.PlayerViewModel
import com.practicum.playlistmaker3.search.ui.TrackUi
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_nothing_found_120),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.no_favorites), fontSize = 16.sp)
            }
        }
        is FavoritesState.Content -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(state.tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }
        else -> {}
    }
}

@Composable
fun TrackItem(
    track: TrackUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.getCoverArtwork(),
            contentDescription = null,
            modifier = Modifier.size(45.dp),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_cover_312),
            error = painterResource(R.drawable.placeholder_cover_312)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artistName} • ${track.formattedTime}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true, name = "Favorites - Empty")
@Composable
fun FavoritesScreenEmptyPreview() {
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState.Empty,
            onTrackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Favorites - Content")
@Composable
fun FavoritesScreenContentPreview() {
    val sampleTracks = listOf(
        TrackUi(
            trackId = 1,
            trackName = "Название трека",
            artistName = "Исполнитель",
            trackTimeMillis = 180000,
            artworkUrl100 = "",
            collectionName = "Альбом",
            releaseDate = "2023",
            primaryGenreName = "Рок",
            country = "Россия",
            previewUrl = null,
            isFavorite = true
        ),
        TrackUi(
            trackId = 2,
            trackName = "Другой трек",
            artistName = "Другой исполнитель",
            trackTimeMillis = 240000,
            artworkUrl100 = "",
            collectionName = null,
            releaseDate = null,
            primaryGenreName = null,
            country = null,
            previewUrl = null,
            isFavorite = true
        )
    )
    MaterialTheme {
        FavoritesScreenContent(
            state = FavoritesState.Content(sampleTracks),
            onTrackClick = {}
        )
    }
}