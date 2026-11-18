package com.practicum.playlistmaker3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF3772E7)
private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000)
private val WindowBackgroundLight = Color(0xFFFFFFFF)
private val WindowBackgroundDark = Color(0xFF1A1B22)
private val SearchAreaLight = Color(0xFFE6E8EB)
private val SearchAreaDark = Color(0xFFFFFFFF)
private val SingerSearchLight = Color(0xFFAEAFB4)
private val SingerSearchDark = Color(0xFFFFFFFF)
private val IconColorLight = Color(0xFFAEAFB4)
private val IconColorDark = Color(0xFFFFFFFF)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Blue,
            onPrimary = White,
            background = WindowBackgroundDark,
            onBackground = White,
            surface = WindowBackgroundDark,
            onSurface = White,
            surfaceVariant = SearchAreaDark,
            onSurfaceVariant = SingerSearchDark,
            error = Color.Red,
            outline = IconColorDark
        )
    } else {
        lightColorScheme(
            primary = Blue,
            onPrimary = White,
            background = WindowBackgroundLight,
            onBackground = Black,
            surface = WindowBackgroundLight,
            onSurface = Black,
            surfaceVariant = SearchAreaLight,
            onSurfaceVariant = SingerSearchLight,
            error = Color.Red,
            outline = IconColorLight
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}