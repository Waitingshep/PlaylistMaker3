
package com.practicum.playlistmaker3.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val ysDisplayRegular = FontFamily(
        Font(
            R.font.ys_display_regular,
            FontWeight.Normal
        )
    )

    Column(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars
        )
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor
        )

        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            containerColor = backgroundColor,
            tonalElevation = 0.dp,
            contentPadding = PaddingValues(0.dp),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_search_mode_24,
                    label = stringResource(R.string.search),
                    selected = currentRoute == Routes.SEARCH,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    fontFamily = ysDisplayRegular,
                    onClick = {
                        onNavigate(Routes.SEARCH)
                    }
                )

                BottomNavigationItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_media_mode_24,
                    label = stringResource(R.string.media),
                    selected = currentRoute == Routes.MEDIA,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    fontFamily = ysDisplayRegular,
                    onClick = {
                        onNavigate(Routes.MEDIA)
                    }
                )

                BottomNavigationItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_settings_mode_24,
                    label = stringResource(R.string.settings),
                    selected = currentRoute == Routes.SETTINGS,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    fontFamily = ysDisplayRegular,
                    onClick = {
                        onNavigate(Routes.SETTINGS)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    val itemColor = if (selected) {
        selectedColor
    } else {
        unselectedColor
    }

    Column(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = itemColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            color = itemColor,
            maxLines = 1
        )
    }
}
