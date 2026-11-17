
package com.practicum.playlistmaker3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.practicum.playlistmaker3.R

@Composable
fun themedPainterResource(@DrawableRes iconRes: Int): Painter {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val selectedRes = if (isDark) {
        when (iconRes) {
            R.drawable.ic_add_to_playlist_51 ->
                R.drawable.ic_add_to_playlist_51_dark

            R.drawable.ic_error_120 ->
                R.drawable.ic_error_120_dark

            R.drawable.ic_favorite_51 ->
                R.drawable.ic_favorite_51_dark

            R.drawable.ic_favorite_filled_51 ->
                R.drawable.ic_favorite_filled_51_dark

            R.drawable.ic_nothing_found_120 ->
                R.drawable.ic_nothing_found_120_dark

            R.drawable.ic_pause_button_100 ->
                R.drawable.ic_pause_button_100_dark

            R.drawable.ic_play_button_100 ->
                R.drawable.ic_play_button_100_dark

            R.drawable.placeholder_cover_312 ->
                R.drawable.placeholder_cover_312_dark

            else -> iconRes
        }
    } else {
        iconRes
    }

    return painterResource(id = selectedRes)
}
