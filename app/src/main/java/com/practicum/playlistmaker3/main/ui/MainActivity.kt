package com.practicum.playlistmaker3.main.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.media.ui.MediaScreen
import com.practicum.playlistmaker3.playlist.ui.CreatePlaylistScreen
import com.practicum.playlistmaker3.playlist.ui.EditPlaylistScreen
import com.practicum.playlistmaker3.playlist.ui.PlaylistScreen
import com.practicum.playlistmaker3.player.ui.PlayerScreen
import com.practicum.playlistmaker3.search.ui.SearchScreen
import com.practicum.playlistmaker3.settings.ui.SettingsScreen
import com.practicum.playlistmaker3.ui.theme.PlaylistMakerTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlaylistMakerApp()
                }
            }
        }
    }
}

object Routes {
    const val SEARCH = "search"
    const val MEDIA = "media"
    const val SETTINGS = "settings"
    const val PLAYER = "player"
    const val CREATE_PLAYLIST = "create_playlist"
    const val PLAYLIST = "playlist"
    const val EDIT_PLAYLIST = "edit_playlist"

    fun playlist(playlistId: Long) = "$PLAYLIST/$playlistId"
    fun editPlaylist(playlistId: Long) = "$EDIT_PLAYLIST/$playlistId"
}

@Composable
fun PlaylistMakerApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(Routes.SEARCH, Routes.MEDIA, Routes.SETTINGS)
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBarContent(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MEDIA,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SEARCH) {
                SearchScreen(navController)
            }
            composable(Routes.MEDIA) {
                MediaScreen(navController)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
            composable(Routes.PLAYER) {
                PlayerScreen(navController)
            }
            composable(Routes.CREATE_PLAYLIST) {
                CreatePlaylistScreen(navController)
            }
            composable(
                route = "${Routes.PLAYLIST}/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistScreen(navController, playlistId)
            }
            composable(
                route = "${Routes.EDIT_PLAYLIST}/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                EditPlaylistScreen(navController, playlistId)
            }
        }
    }
}

@Composable
fun BottomNavigationBarContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurface
    val dividerColor = MaterialTheme.colorScheme.outline

    val ysDisplayRegular = FontFamily(Font(R.font.ys_display_regular, FontWeight.Normal))

    Column {
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        BottomAppBar(
            modifier = Modifier.height(56.dp),
            containerColor = backgroundColor,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(Routes.SEARCH) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_mode_24),
                        contentDescription = null,
                        tint = if (currentRoute == Routes.SEARCH) selectedColor else unselectedColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.search),
                        fontSize = 10.sp,
                        fontFamily = ysDisplayRegular,
                        fontWeight = FontWeight.Normal,
                        color = if (currentRoute == Routes.SEARCH) selectedColor else unselectedColor
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(Routes.MEDIA) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_media_mode_24),
                        contentDescription = null,
                        tint = if (currentRoute == Routes.MEDIA) selectedColor else unselectedColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.media),
                        fontSize = 10.sp,
                        fontFamily = ysDisplayRegular,
                        fontWeight = FontWeight.Normal,
                        color = if (currentRoute == Routes.MEDIA) selectedColor else unselectedColor
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(Routes.SETTINGS) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings_mode_24),
                        contentDescription = null,
                        tint = if (currentRoute == Routes.SETTINGS) selectedColor else unselectedColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.settings),
                        fontSize = 10.sp,
                        fontFamily = ysDisplayRegular,
                        fontWeight = FontWeight.Normal,
                        color = if (currentRoute == Routes.SETTINGS) selectedColor else unselectedColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Search selected")
@Composable
fun BottomNavSearchPreview() {
    PlaylistMakerTheme {
        BottomNavigationBarContent(
            currentRoute = Routes.SEARCH,
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Media selected")
@Composable
fun BottomNavMediaPreview() {
    PlaylistMakerTheme {
        BottomNavigationBarContent(
            currentRoute = Routes.MEDIA,
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Settings selected")
@Composable
fun BottomNavSettingsPreview() {
    PlaylistMakerTheme {
        BottomNavigationBarContent(
            currentRoute = Routes.SETTINGS,
            onNavigate = {}
        )
    }
}