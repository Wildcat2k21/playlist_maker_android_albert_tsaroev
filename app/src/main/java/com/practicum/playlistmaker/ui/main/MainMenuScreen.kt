package com.practicum.playlistmaker.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.utils.ButtonSample
import com.practicum.playlistmaker.ui.utils.IconType

@Composable
fun MenuScreen(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(240, 84, 139))
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp),
            text = stringResource(R.string.app_name),
            fontSize = 22.sp,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.SansSerif
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(Color(244, 212, 221))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
                    .background(Color(249, 206, 218))
            ) {
                ButtonSample(
                    leadingIcon = IconType.PainterIcon(painterResource(R.drawable.search_icon)),
                    trailingIcon = IconType.ImageVectorIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight),
                    contentDescription = stringResource(R.string.search),
                    onClick = onSearchClick,
                )
                ButtonSample(
                    leadingIcon = IconType.PainterIcon(painterResource(R.drawable.playlists_icon)),
                    trailingIcon = IconType.ImageVectorIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight),
                    contentDescription = stringResource(R.string.playlists),
                    onClick = onPlaylistsClick
                )
                ButtonSample(
                    leadingIcon = IconType.PainterIcon(painterResource(R.drawable.favourites_icon)),
                    trailingIcon = IconType.ImageVectorIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight),
                    contentDescription = stringResource(R.string.favourites),
                    onClick = onFavoritesClick,
                )
                ButtonSample(
                    leadingIcon = IconType.PainterIcon(painterResource(R.drawable.settings_icon)),
                    trailingIcon = IconType.ImageVectorIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight),
                    contentDescription = stringResource(R.string.settings),
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun MenuScreenPreview() {
    MenuScreen(
        onSearchClick = { },
        onSettingsClick = { },
        onPlaylistsClick = { },
        onFavoritesClick = { },
    )
}