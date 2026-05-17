package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppButtonBar(
                context = context,
                text = stringResource(R.string.settings),
                onClick = onBackClick,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
        ) {
            SettingsRow(
                text = stringResource(R.string.dark_theme),
                onClick = onToggleTheme,
            ) {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleTheme() },
                )
            }
            SettingsRow(
                text = stringResource(R.string.share_app),
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_text))
                        type = "text/plain"
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, context.getString(R.string.share_app)),
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SettingsRow(
                text = stringResource(R.string.message_to_support),
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.dev_email)))
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text))
                    }
                    context.startActivity(intent)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.support_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SettingsRow(
                text = stringResource(R.string.user_agreement),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = context.getString(R.string.agreement_url).toUri()
                    }
                    context.startActivity(intent)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_forward_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    text: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        trailingContent()
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        SettingsScreen(onBackClick = {})
    }
}
