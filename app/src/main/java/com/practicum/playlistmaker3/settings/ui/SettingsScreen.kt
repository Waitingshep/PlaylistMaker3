package com.practicum.playlistmaker3.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker3.R
import com.practicum.playlistmaker3.settings.domain.models.ThemeMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.observeAsState()

    val isDarkTheme = when (val s = state) {
        is SettingsState.ThemeLoaded -> s.mode == ThemeMode.DARK
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.black_theme),
                fontSize = 16.sp
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isChecked ->
                    val newMode = if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                    viewModel.setTheme(newMode)
                }
            )
        }

        SettingsItem(
            text = stringResource(R.string.share_app),
            iconRes = R.drawable.ic_share_mode_24,
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_url))
                }
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)))
            }
        )

        SettingsItem(
            text = stringResource(R.string.support),
            iconRes = R.drawable.ic_support_mode_24,
            onClick = {
                val message = context.getString(R.string.support_message)
                val subject = context.getString(R.string.support_subject)
                val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
                    putExtra(Intent.EXTRA_TEXT, message)
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                }
                context.startActivity(supportIntent)
            }
        )

        SettingsItem(
            text = stringResource(R.string.agreement),
            iconRes = R.drawable.ic_arrow_forward_24,
            onClick = {
                val agreementUrl = context.getString(R.string.agreement_url)
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl)))
            }
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, fontSize = 16.sp)
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}