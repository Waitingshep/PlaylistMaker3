
package com.practicum.playlistmaker3.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import com.practicum.playlistmaker3.settings.ui.SettingsState
import com.practicum.playlistmaker3.settings.ui.SettingsViewModel
import com.practicum.playlistmaker3.ui.PlaylistMakerApp
import com.practicum.playlistmaker3.ui.theme.PlaylistMakerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = koinViewModel(
                viewModelStoreOwner = this@MainActivity
            )

            val themeState by settingsViewModel.state.observeAsState()

            val isDark = when (val s = themeState) {
                is SettingsState.ThemeLoaded -> s.mode == ThemeMode.DARK
                else -> isSystemInDarkTheme()
            }

            PlaylistMakerTheme(darkTheme = isDark) {
                PlaylistMakerApp(isDark = isDark)
            }
        }
    }
}
