package com.practicum.playlistmaker.ui.playlist

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.ui.search.TrackAppListItem
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlaylistAppScreen(
    modifier: Modifier = Modifier,
    playlistViewModel: PlaylistAppViewModel,
    playlistsAppViewModel: PlaylistsAppViewModel,
    onTrackClick: (TrackApp) -> Unit,
    navigateBack: () -> Unit,
) {
    val playlist by playlistViewModel.playlist.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    PlaylistAppScreenContent(
        modifier = modifier,
        playlist = playlist,
        onTrackClick = onTrackClick,
        onTrackLongClick = { track ->
            coroutineScope.launch {
                playlistsAppViewModel.deleteTrackFromPlaylist(track)

                Toast.makeText(
                    context,
                    context.getString(
                        R.string.removed_from_playlist_message,
                        playlist?.name.orEmpty(),
                    ),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        },
        onDeletePlaylist = {
            playlist?.id?.let { playlistId ->
                coroutineScope.launch {
                    playlistsAppViewModel.deleteAppPlaylistById(playlistId)

                    Toast.makeText(
                        context,
                        context.getString(R.string.playlist_deleted_message),
                        Toast.LENGTH_SHORT,
                    ).show()

                    navigateBack()
                }
            }
        },
        navigateBack = navigateBack,
    )
}

@Composable
internal fun PlaylistAppScreenContent(
    modifier: Modifier = Modifier,
    playlist: PlaylistApp?,
    onTrackClick: (TrackApp) -> Unit,
    onTrackLongClick: (TrackApp) -> Unit,
    onDeletePlaylist: () -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    var isDeletePlaylistDialogVisible by remember {
        mutableStateOf(false)
    }

    var trackToDelete by remember {
        mutableStateOf<TrackApp?>(null)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppButtonBar(
                context = context,
                text = playlist?.name ?: stringResource(R.string.playlists),
                onClick = navigateBack,
                actions = {
                    if (playlist != null) {
                        IconButton(
                            onClick = {
                                isDeletePlaylistDialogVisible = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_playlist),
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->

        when (playlist) {

            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.playlist_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            else -> {

                val tracksCount = playlist.tracks.size
                val totalMinutes = calculatePlaylistMinutes(playlist.tracks)

                val year = formatPlaylistYear(playlist.createdAt)

                val minutesLabel = pluralStringResource(
                    id = R.plurals.minutes_count,
                    count = totalMinutes,
                    totalMinutes,
                )

                val tracksLabel = pluralStringResource(
                    id = R.plurals.tracks_count,
                    count = tracksCount,
                    tracksCount,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Box(
                        modifier = Modifier.size(300.dp),
                        contentAlignment = Alignment.Center,
                    ) {

                        playlist.coverPath
                            .takeIf { it.isNotBlank() }
                            ?.let(::File)
                            ?.let { coverFile ->

                                AsyncImage(
                                    model = coverFile,
                                    contentDescription = stringResource(R.string.playlist_name),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }

                            ?: Image(
                                painter = painterResource(R.drawable.add_playlist_photo),
                                contentDescription = stringResource(R.string.playlist_name),
                            )
                    }

                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            ),
                    )

                    if (year.isNotBlank()) {
                        Text(
                            text = year,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        )
                    }

                    Text(
                        text = "$minutesLabel · $tracksLabel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            ),
                    )

                    playlist.description
                        .takeIf { it.isNotBlank() }
                        ?.let { description ->

                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp,
                                    ),
                            )
                        }

                    if (playlist.tracks.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {

                            Text(
                                text = stringResource(R.string.playlist_empty),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                    } else {

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {

                            items(
                                items = playlist.tracks,
                                key = { track -> track.id },
                            ) { track ->

                                TrackAppListItem(
                                    track = track,
                                    onClick = {
                                        onTrackClick(track)
                                    },
                                    onLongClick = {
                                        trackToDelete = track
                                    },
                                )

                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isDeletePlaylistDialogVisible) {

        AlertDialog(
            onDismissRequest = {
                isDeletePlaylistDialogVisible = false
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
                        isDeletePlaylistDialogVisible = false
                        onDeletePlaylist()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete_action),
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        isDeletePlaylistDialogVisible = false
                    },
                ) {
                    Text(
                        text = stringResource(R.string.cancel_action),
                    )
                }
            },
        )
    }

    trackToDelete?.let { track ->

        AlertDialog(
            onDismissRequest = {
                trackToDelete = null
            },

            title = {
                Text(
                    text = stringResource(R.string.delete_track_title),
                )
            },

            text = {
                Text(
                    text = stringResource(R.string.delete_track_message),
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        onTrackLongClick(track)
                        trackToDelete = null
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete_action),
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        trackToDelete = null
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

private val previewPlaylistWithTracks = PlaylistApp(
    id = 1L,
    name = "Мой плейлист",
    description = "Описание для превью — несколько строк, чтобы проверить отступы.",
    createdAt = System.currentTimeMillis(),
    tracks = listOf(
        TrackApp(
            id = 101L,
            trackName = "Владивосток 2000",
            artistName = "Мумий Троль",
            trackTime = "3:34",
            image = "",
            favorite = false,
            playlistId = 1L,
        ),
        TrackApp(
            id = 102L,
            trackName = "Группа крови",
            artistName = "Кино",
            trackTime = "4:45",
            image = "",
            favorite = true,
            playlistId = 1L,
        ),
    ),
)

@Preview(showBackground = true, name = "С треками")
@Composable
private fun PlaylistAppScreenPreviewWithTracks() {
    PlaylistMakerTheme(dynamicColor = false) {

        PlaylistAppScreenContent(
            playlist = previewPlaylistWithTracks,
            onTrackClick = {},
            onTrackLongClick = {},
            onDeletePlaylist = {},
            navigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Пустой плейлист")
@Composable
private fun PlaylistAppScreenPreviewEmpty() {
    PlaylistMakerTheme(dynamicColor = false) {

        PlaylistAppScreenContent(
            playlist = PlaylistApp(
                id = 2L,
                name = "Пустой",
                description = "",
                createdAt = System.currentTimeMillis(),
                tracks = emptyList(),
            ),
            onTrackClick = {},
            onTrackLongClick = {},
            onDeletePlaylist = {},
            navigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Не найден")
@Composable
private fun PlaylistAppScreenPreviewNotFound() {
    PlaylistMakerTheme(dynamicColor = false) {

        PlaylistAppScreenContent(
            playlist = null,
            onTrackClick = {},
            onTrackLongClick = {},
            onDeletePlaylist = {},
            navigateBack = {},
        )
    }
}

private fun formatPlaylistYear(createdAt: Long): String {

    if (createdAt <= 0L) return ""

    return SimpleDateFormat(
        "yyyy",
        Locale.getDefault(),
    ).format(Date(createdAt))
}

private fun calculatePlaylistMinutes(
    tracks: List<TrackApp>,
): Int {

    val totalSeconds = tracks.sumOf { track ->
        parseTrackDurationSeconds(track.trackTime)
    }

    return totalSeconds / 60
}

private fun parseTrackDurationSeconds(
    trackTime: String,
): Int {

    val parts = trackTime.split(':')

    if (parts.size != 2) return 0

    val minutes = parts[0].toIntOrNull() ?: return 0
    val seconds = parts[1].toIntOrNull() ?: return 0

    return minutes * 60 + seconds
}