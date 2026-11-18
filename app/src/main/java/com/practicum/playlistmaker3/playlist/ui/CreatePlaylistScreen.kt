package com.practicum.playlistmaker3.playlist.ui

import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.practicum.playlistmaker3.R
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreatePlaylistScreen(
    navController: NavController,
    viewModel: CreatePlaylistViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.observeAsState(CreatePlaylistState())
    val snackbarHostState = remember { SnackbarHostState() }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val path = saveImageToPrivateStorage(context, uri)
                viewModel.setCoverPath(path)
                viewModel.updateCover(uri)
            }
        }
    }

    BackHandler {
        if (viewModel.onBackPressed()) {
            showDiscardDialog = true
        } else {
            navController.popBackStack()
        }
    }

    val creationResult = state.creationResult
    LaunchedEffect(creationResult) {
        if (creationResult != null && creationResult > 0) {
            val message = if (state.isEditMode) {
                "Плейлист «${state.name}» сохранён"
            } else {
                "Плейлист «${state.name}» создан"
            }
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
            viewModel.resetCreationResult()
            navController.popBackStack()
        } else if (creationResult != null) {
            isSaving = false
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_title)) },
            text = { Text(stringResource(R.string.discard_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDiscardDialogConfirmed()
                    showDiscardDialog = false
                    navController.popBackStack()
                }) {
                    Text(stringResource(R.string.discard_positive))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onDiscardDialogCancelled()
                    showDiscardDialog = false
                }) {
                    Text(stringResource(R.string.discard_negative))
                }
            }
        )
    }

    CreatePlaylistScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = {
            if (viewModel.onBackPressed()) {
                showDiscardDialog = true
            } else {
                navController.popBackStack()
            }
        },
        onNameChange = { viewModel.updateName(it) },
        onDescriptionChange = { viewModel.updateDescription(it) },
        onCoverClick = {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        },
        onCreateClick = {
            if (!isSaving) {
                isSaving = true
                viewModel.createPlaylist()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreenContent(
    state: CreatePlaylistState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCoverClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = onCreateClick,
                enabled = state.isCreateEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = stringResource(
                        if (state.isEditMode) R.string.save else R.string.create
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(
                        if (state.isEditMode) R.string.edit_playlist_title else R.string.new_playlist
                    ),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCoverClick),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.dashed_border),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                if (state.coverPath != null || state.coverUri != null) {
                    AsyncImage(
                        model = state.coverUri ?: state.coverPath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_add_playlist_cover_80),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.playlist_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.playlist_description_hint)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun saveImageToPrivateStorage(context: android.content.Context, sourceUri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Preview(showBackground = true, name = "Create Playlist - Light")
@Composable
fun CreatePlaylistScreenLightPreview() {
    MaterialTheme {
        CreatePlaylistScreenContent(
            state = CreatePlaylistState(name = "Мой плейлист", isCreateEnabled = true),
            onBackClick = {},
            onNameChange = {},
            onDescriptionChange = {},
            onCoverClick = {},
            onCreateClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Create Playlist - Dark", backgroundColor = 0xFF1A1B22)
@Composable
fun CreatePlaylistScreenDarkPreview() {
    MaterialTheme {
        CreatePlaylistScreenContent(
            state = CreatePlaylistState(name = "Мой плейлист", isCreateEnabled = true),
            onBackClick = {},
            onNameChange = {},
            onDescriptionChange = {},
            onCoverClick = {},
            onCreateClick = {}
        )
    }
}