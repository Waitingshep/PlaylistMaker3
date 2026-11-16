package com.practicum.playlistmaker3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.playlistmaker3.media.ui.MediaScreen
import com.practicum.playlistmaker3.playlist.ui.CreatePlaylistScreen
import com.practicum.playlistmaker3.playlist.ui.EditPlaylistScreen
import com.practicum.playlistmaker3.playlist.ui.PlaylistScreen
import com.practicum.playlistmaker3.player.ui.PlayerScreen
import com.practicum.playlistmaker3.search.ui.SearchScreen
import com.practicum.playlistmaker3.settings.ui.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistMakerApp(isDark: Boolean) {
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
                PlaylistScreen(navController, playlistId, isDark = isDark)
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