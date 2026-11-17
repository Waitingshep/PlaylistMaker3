package com.practicum.playlistmaker3.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.ui.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(
    navController: NavController,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState()

    PlaylistsScreenContent(
        state = state,
        onCreateClick = { navController.navigate(Routes.CREATE_PLAYLIST) },
        onPlaylistClick = { playlist ->
            navController.navigate("${Routes.PLAYLIST}/${playlist.id}")
        }
    )
}

@Composable
fun PlaylistsScreenContent(
    state: PlaylistsState?,
    onCreateClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onCreateClick,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_playlist),
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 16.sp,
                    maxLines = 1
                )
            }
        }

        when (state) {
            is PlaylistsState.Empty -> {
                Spacer(modifier = Modifier.height(46.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.ic_nothing_found_120),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.no_playlists), fontSize = 16.sp)
                    }
                }
            }
            is PlaylistsState.Content -> {
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.playlists) { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist) }
                        )
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_cover_playlist),
            error = painterResource(R.drawable.placeholder_cover_playlist)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = playlist.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = pluralStringResource(R.plurals.tracks_count, playlist.trackCount, playlist.trackCount),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true, name = "Playlists - Empty")
@Composable
fun PlaylistsScreenEmptyPreview() {
    MaterialTheme {
        PlaylistsScreenContent(
            state = PlaylistsState.Empty,
            onCreateClick = {},
            onPlaylistClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Playlists - Content")
@Composable
fun PlaylistsScreenContentPreview() {
    val samplePlaylists = listOf(
        Playlist(
            id = 1,
            name = "Мой плейлист",
            description = "Описание",
            coverPath = null,
            trackIds = listOf(1, 2, 3),
            trackCount = 3
        ),
        Playlist(
            id = 2,
            name = "Рок",
            description = null,
            coverPath = null,
            trackIds = emptyList(),
            trackCount = 0
        ),
        Playlist(
            id = 3,
            name = "Для тренировки",
            description = "Энергичные треки",
            coverPath = null,
            trackIds = listOf(4),
            trackCount = 1
        )
    )
    MaterialTheme {
        PlaylistsScreenContent(
            state = PlaylistsState.Content(samplePlaylists),
            onCreateClick = {},
            onPlaylistClick = {}
        )
    }
}