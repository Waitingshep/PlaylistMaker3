package com.practicum.playlistmaker3.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker3.R

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