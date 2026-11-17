package com.practicum.playlistmaker3.search.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.player.ui.PlayerViewModel
import com.practicum.playlistmaker3.ui.Routes
import com.practicum.playlistmaker3.ui.components.EmptyPlaceholder
import com.practicum.playlistmaker3.ui.components.TrackItem
import com.practicum.playlistmaker3.ui.components.themedPainterResource
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = koinViewModel(),
    playerViewModel: PlayerViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.observeAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (searchQuery.isEmpty()) {
            viewModel.restoreSearchState()
        }
    }
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            delay(300L)
            viewModel.searchDebounce(searchQuery)
        } else {
            viewModel.loadHistory()
        }
    }
    SearchScreenContent(
        state = state,
        searchQuery = searchQuery,
        onQueryChange = { query -> searchQuery = query },
        onClearQuery = {
            searchQuery = ""
            hideKeyboard(context)
            viewModel.clearSearch()
        },
        onRetry = { viewModel.retryLastSearch() },
        onClearHistory = { viewModel.clearHistory() },
        onTrackClick = { track ->
            viewModel.addToHistory(track)
            playerViewModel.setTrackToPlay(track)
            navController.navigate(Routes.PLAYER)
        }
    )
}

@Composable
fun SearchScreenContent(
    state: SearchState?,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onClearHistory: () -> Unit,
    onTrackClick: (TrackUi) -> Unit
) {
    val fieldBg = colorResource(R.color.search_field_background)
    val fieldText = colorResource(R.color.search_field_icon)
    val inputTextColor = colorResource(R.color.search_field_text)
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.search),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .height(36.dp).background(fieldBg, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_search_mode_16),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(fieldText)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f).height(36.dp).padding(vertical = 8.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = inputTextColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {}),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            fontSize = 16.sp,
                            color = fieldText
                        )
                    }
                    innerTextField()
                }
            )
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = onClearQuery, modifier = Modifier.size(16.dp)) {
                    Image(
                        painter = painterResource(R.drawable.ic_cancel_16),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(fieldText)
                    )
                }
            }
        }
        when (val s = state) {
            is SearchState.Loading -> {
                Spacer(modifier = Modifier.height(102.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchState.Content -> {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(s.tracks) { track ->
                        TrackItem(track = track, onClick = { onTrackClick(track) })
                    }
                }
            }
            is SearchState.History -> {
                if (s.tracks.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = stringResource(R.string.search_history_title),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 24.dp, bottom = 16.dp)
                            )
                        }
                        items(s.tracks) { track ->
                            TrackItem(track = track, onClick = { onTrackClick(track) })
                        }
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = onClearHistory,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.clear_history),
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is SearchState.Empty -> {
                Spacer(modifier = Modifier.height(102.dp))
                EmptyPlaceholder(
                    text = stringResource(R.string.nothing_found),
                    iconRes = R.drawable.ic_nothing_found_120
                )
            }
            is SearchState.Error -> {
                Spacer(modifier = Modifier.height(102.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = themedPainterResource(R.drawable.ic_error_120),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.something_went_wrong),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.update),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
            else -> {}
        }
    }
}

private fun hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(
        (context as? ComponentActivity)?.currentFocus?.windowToken,
        0
    )
}
