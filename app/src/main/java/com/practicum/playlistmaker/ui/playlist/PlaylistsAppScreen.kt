package com.practicum.playlistmaker.ui.playlist

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar
import java.io.File

@Composable
fun PlaylistAppListItem(
    playlist: PlaylistApp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        val coverModel = playlist.coverPath
            .takeIf { it.isNotBlank() }
            ?.let(::File)

        Surface(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
        ) {

            if (coverModel == null) {

                Image(
                    modifier = Modifier.padding(8.dp),
                    painter = painterResource(R.drawable.ic_music),
                    contentDescription = playlist.name,
                )

            } else {

                AsyncImage(
                    model = coverModel,
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {

            Text(
                text = playlist.name,
                fontSize = 16.sp,
            )

            Text(
                text = pluralStringResource(
                    id = R.plurals.tracks_count,
                    count = playlist.tracks.size,
                    playlist.tracks.size,
                ),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun PlaylistsAppScreen(
    modifier: Modifier = Modifier,
    playlistsAppViewModel: PlaylistsAppViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val playlists by playlistsAppViewModel
        .playlists
        .collectAsState(emptyList())

    val context = LocalContext.current

    val playlistToDelete = remember {
        mutableStateOf<PlaylistApp?>(null)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),

        topBar = {

            TopAppButtonBar(
                text = stringResource(R.string.playlists),
                context = context,
                onClick = navigateBack,
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = addNewPlaylist,
                shape = CircleShape,
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_playlist),
                )
            }
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

            items(
                items = playlists,
                key = { playlist -> playlist.id },
            ) { playlist ->

                PlaylistAppListItem(
                    playlist = playlist,

                    onClick = {
                        navigateToPlaylist(playlist.id)
                    },

                    onLongClick = {
                        playlistToDelete.value = playlist
                    },
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                )
            }
        }
    }

    playlistToDelete.value?.let { playlist ->

        AlertDialog(
            onDismissRequest = {
                playlistToDelete.value = null
            },

            title = {

                Text(
                    text = stringResource(R.string.delete_playlist_title),
                )
            },

            text = {

                Text(
                    text = stringResource(R.string.delete_playlist_message),
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val playlistId = playlist.id

                        playlistToDelete.value = null

                        coroutineScope.launch {

                            playlistsAppViewModel.deleteAppPlaylistById(
                                playlistId,
                            )
                        }
                    }
                ) {

                    Text(
                        text = stringResource(R.string.delete_action),
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        playlistToDelete.value = null
                    },
                ) {

                    Text(
                        text = stringResource(R.string.cancel_action),
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaylistsAppScreenPreview() {

    val playlistsAppViewModel = remember {
        PlaylistsAppViewModel()
    }

    PlaylistsAppScreen(
        playlistsAppViewModel = playlistsAppViewModel,
        addNewPlaylist = {},
        navigateToPlaylist = {},
        navigateBack = {},
    )
}