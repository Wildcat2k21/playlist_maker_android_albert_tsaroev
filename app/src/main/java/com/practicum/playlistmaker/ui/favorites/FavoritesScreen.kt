@file:Suppress("ViewModelConstructorInComposable")

package com.practicum.playlistmaker.ui.favorites

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.ui.playlist.PlaylistsAppViewModel
import com.practicum.playlistmaker.ui.search.TrackAppListItem
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    playlistsAppViewModel: PlaylistsAppViewModel,
    onTrackClick: (TrackApp) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val favoriteTracks by playlistsAppViewModel.favoriteList.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    var trackToRemoveFromFavorites by remember { mutableStateOf<TrackApp?>(null) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppButtonBar(
                context = context,
                text = stringResource(R.string.favourites),
                onClick = navigateBack,
            )
        },
    ) { padding ->
        if (favoriteTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.favorites_screen_empty),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                items(favoriteTracks.size) { index ->
                    val track = favoriteTracks[index]
                    TrackAppListItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                        onLongClick = {
                            trackToRemoveFromFavorites = track
                        },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (trackToRemoveFromFavorites != null) {
        AlertDialog(
            onDismissRequest = { trackToRemoveFromFavorites = null },
            title = { Text(text = stringResource(R.string.remove_from_favorites_title)) },
            text = { Text(text = stringResource(R.string.remove_from_favorites_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val track = trackToRemoveFromFavorites
                        trackToRemoveFromFavorites = null
                        if (track != null) {
                            coroutineScope.launch {
                                playlistsAppViewModel.toggleFavorite(track, isFavorite = false)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.removed_from_favorites_message),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToRemoveFromFavorites = null }) {
                    Text(text = stringResource(R.string.cancel_action))
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    PlaylistMakerTheme(dynamicColor = false) {
        FavoritesScreen(
            playlistsAppViewModel = PlaylistsAppViewModel(),
            onTrackClick = {},
            navigateBack = {},
        )
    }
}