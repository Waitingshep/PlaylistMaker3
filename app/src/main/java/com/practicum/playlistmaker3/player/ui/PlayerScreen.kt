package com.practicum.playlistmaker3.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.ui.Routes
import com.practicum.playlistmaker3.player.service.PlayerService
import com.practicum.playlistmaker3.playlist.domain.models.Playlist
import com.practicum.playlistmaker3.search.domain.models.Track
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.observeAsState()
    val playlists by viewModel.playlists.observeAsState(emptyList())
    val showBottomSheet by viewModel.showPlaylistBottomSheet.observeAsState(false)
    val playlistAddStatus by viewModel.playlistAddStatus.observeAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val currentTrack = when (val s = state) {
        is PlayerState.Content -> s.track
        is PlayerState.Playing -> s.track
        is PlayerState.Paused -> s.track
        else -> null
    }

    val trackId = currentTrack?.trackId
    DisposableEffect(trackId) {
        if (currentTrack == null) return@DisposableEffect onDispose { }
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlayerService.PlayerBinder
                viewModel.onServiceConnected(binder.getService())
            }
            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        val intent = Intent(context, PlayerService::class.java).apply {
            putExtra(PlayerService.EXTRA_TRACK_ID, currentTrack.trackId)
            putExtra(PlayerService.EXTRA_TRACK_NAME, currentTrack.trackName)
            putExtra(PlayerService.EXTRA_ARTIST_NAME, currentTrack.artistName)
            putExtra(PlayerService.EXTRA_TRACK_TIME_MILLIS, currentTrack.trackTimeMillis)
            putExtra(PlayerService.EXTRA_ARTWORK_URL_100, currentTrack.artworkUrl100)
            putExtra(PlayerService.EXTRA_COLLECTION_NAME, currentTrack.collectionName)
            putExtra(PlayerService.EXTRA_RELEASE_DATE, currentTrack.releaseDate)
            putExtra(PlayerService.EXTRA_PRIMARY_GENRE_NAME, currentTrack.primaryGenreName)
            putExtra(PlayerService.EXTRA_COUNTRY, currentTrack.country)
            putExtra(PlayerService.EXTRA_PREVIEW_URL, currentTrack.previewUrl)
            putExtra(PlayerService.EXTRA_IS_FAVORITE, currentTrack.isFavorite)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(connection) }
    }

    DisposableEffect(Unit) {
        viewModel.onUiResumed()
        onDispose { viewModel.onUiPaused() }
    }

    val isPlaying = state is PlayerState.Playing
    val currentPosition = when (val s = state) {
        is PlayerState.Playing -> s.currentPosition
        is PlayerState.Paused -> s.currentPosition
        else -> 0
    }
    val isFavorite = when (val s = state) {
        is PlayerState.Content -> s.isFavorite
        is PlayerState.Playing -> s.isFavorite
        is PlayerState.Paused -> s.isFavorite
        else -> false
    }

    LaunchedEffect(playlistAddStatus) {
        playlistAddStatus?.let { status ->
            val message = when (status) {
                is PlaylistAddStatus.Success -> context.getString(R.string.added_to_playlist, status.playlistName)
                is PlaylistAddStatus.AlreadyExists -> context.getString(R.string.track_already_in_playlist, status.playlistName)
            }
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearPlaylistAddStatus()
        }
    }

    PlayerScreenContent(
        currentTrack = currentTrack,
        isPlaying = isPlaying,
        currentPosition = currentPosition,
        isFavorite = isFavorite,
        playlists = playlists,
        showBottomSheet = showBottomSheet,
        onBackClick = {
            viewModel.stop()
            navController.popBackStack()
        },
        onPlayPauseClick = {
            when (state) {
                is PlayerState.Playing -> viewModel.pause()
                is PlayerState.Paused -> viewModel.play()
                is PlayerState.Content -> viewModel.play()
                else -> {}
            }
        },
        onFavoriteClick = { viewModel.onFavoriteClicked() },
        onAddToPlaylistClick = { viewModel.showPlaylistBottomSheet() },
        onHideBottomSheet = { viewModel.hidePlaylistBottomSheet() },
        onAddTrackToPlaylist = { playlist -> viewModel.addTrackToPlaylist(playlist) },
        onNavigateToCreatePlaylist = {
            navController.navigate(Routes.CREATE_PLAYLIST)
            viewModel.hidePlaylistBottomSheet()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreenContent(
    currentTrack: Track?,
    isPlaying: Boolean,
    currentPosition: Int,
    isFavorite: Boolean,
    playlists: List<Playlist>,
    showBottomSheet: Boolean,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onHideBottomSheet: () -> Unit,
    onAddTrackToPlaylist: (Playlist) -> Unit,
    onNavigateToCreatePlaylist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (currentTrack != null) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = currentTrack.getCoverArtwork(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_cover_312),
                error = painterResource(R.drawable.placeholder_cover_312)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = currentTrack.trackName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Text(
                text = currentTrack.artistName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(51.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAddToPlaylistClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_add_to_playlist_51),
                        contentDescription = null,
                        modifier = Modifier.size(51.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            if (isPlaying) R.drawable.ic_pause_button_100 else R.drawable.ic_play_button_100
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(51.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onFavoriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_favorite_filled_51 else R.drawable.ic_favorite_51
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(51.dp)
                    )
                }
            }

            Text(
                text = formatTime(currentPosition),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                TrackInfoRow(label = stringResource(R.string.duration), value = currentTrack.formattedTime)
                TrackInfoRow(label = stringResource(R.string.album), value = currentTrack.collectionName ?: "")
                TrackInfoRow(label = stringResource(R.string.year), value = currentTrack.releaseYear ?: "")
                TrackInfoRow(label = stringResource(R.string.genre), value = currentTrack.primaryGenreName ?: "")
                TrackInfoRow(label = stringResource(R.string.country), value = currentTrack.country ?: "")
            }

            Spacer(modifier = Modifier.height(32.dp))
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Трек не загружен", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onHideBottomSheet,
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.add_playlist),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(28.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onNavigateToCreatePlaylist,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(54.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.new_playlist),
                            color = MaterialTheme.colorScheme.surface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                LazyColumn {
                    items(playlists) { playlist ->
                        PlaylistBottomSheetItem(
                            playlist = playlist,
                            onClick = { onAddTrackToPlaylist(playlist) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackInfoRow(label: String, value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 13.sp)
        }
    }
}

@Composable
fun PlaylistBottomSheetItem(playlist: Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_cover_312),
            error = painterResource(R.drawable.placeholder_cover_312)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = playlist.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "${playlist.trackCount} треков", fontSize = 11.sp)
        }
        IconButton(onClick = onClick) {
            Icon(painterResource(R.drawable.ic_arrow_forward_24), contentDescription = null)
        }
    }
}

private fun formatTime(millis: Int): String {
    return String.format("%02d:%02d", millis / 1000 / 60, millis / 1000 % 60)
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1B22)
@Composable
fun PlayerScreenPreview() {
    MaterialTheme {
        PlayerScreenContent(
            currentTrack = null,
            isPlaying = false,
            currentPosition = 0,
            isFavorite = false,
            playlists = emptyList(),
            showBottomSheet = false,
            onBackClick = {},
            onPlayPauseClick = {},
            onFavoriteClick = {},
            onAddToPlaylistClick = {},
            onHideBottomSheet = {},
            onAddTrackToPlaylist = {},
            onNavigateToCreatePlaylist = {}
        )
    }
}