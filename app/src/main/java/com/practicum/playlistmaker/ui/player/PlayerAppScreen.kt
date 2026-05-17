@file:Suppress("ViewModelConstructorInComposable")

package com.practicum.playlistmaker.ui.player

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.ui.navigation.PlayerNavigationArgs
import com.practicum.playlistmaker.ui.playlist.PlaylistsAppViewModel
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerAppScreen(
    modifier: Modifier = Modifier,
    playlistsAppViewModel: PlaylistsAppViewModel,
    navigateBack: () -> Unit,
) {
    val initialTrack = PlayerNavigationArgs.pendingTrack
    val context = LocalContext.current
    val playlists by playlistsAppViewModel.playlists.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var currentTrack by remember(initialTrack?.id) { mutableStateOf(initialTrack) }

    val playerAppViewModel: PlayerAppViewModel = viewModel(
        key = "player_${initialTrack?.id}",
        factory = PlayerAppViewModel.appFactory(initialTrack?.previewUrl.orEmpty()),
    )
    val isPlaying by playerAppViewModel.isPlaying.collectAsState()
    val isReady by playerAppViewModel.isReady.collectAsState()
    val currentPositionMs by playerAppViewModel.currentPositionMs.collectAsState()
    val durationMs by playerAppViewModel.durationMs.collectAsState()

    LaunchedEffect(initialTrack?.id) {
        initialTrack?.let { track ->
            val actualTrack = playlistsAppViewModel.isExist(track)
            currentTrack = actualTrack ?: track
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppButtonBar(
                context = context,
                text = stringResource(R.string.track_details),
                onClick = {
                    PlayerNavigationArgs.pendingTrack = null
                    navigateBack()
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (currentTrack == null) {
                Text(
                    text = stringResource(R.string.player_no_track),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 24.dp),
                )
            } else {
                val track = currentTrack ?: return@Column

                val artworkUrl = track.image.takeIf { it.isNotBlank() }
                AsyncImage(
                    model = artworkUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(top = 16.dp, bottom = 16.dp),
                    placeholder = painterResource(R.drawable.ic_music),
                    error = painterResource(R.drawable.music_not_found),
                    contentScale = ContentScale.Crop,
                )

                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                )
                Text(
                    text = track.artistName,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )

                if (track.previewUrl.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = formatMs(currentPositionMs),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formatMs(if (durationMs > 0) durationMs else parseDuration(track.trackTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Slider(
                        value = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f,
                        onValueChange = { fraction ->
                            playerAppViewModel.seekTo((fraction * durationMs).toInt())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isReady,
                    )

                    IconButton(
                        onClick = { playerAppViewModel.togglePlayPause() },
                        enabled = isReady,
                        modifier = Modifier.size(64.dp),
                    ) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.preview_not_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    IconButton(
                        onClick = {
                            val newFavoriteValue = !track.favorite
                            currentTrack = track.copy(favorite = newFavoriteValue)
                            coroutineScope.launch {
                                playlistsAppViewModel.toggleFavorite(
                                    track = track,
                                    isFavorite = newFavoriteValue,
                                )
                                Toast.makeText(
                                    context,
                                    context.getString(
                                        if (newFavoriteValue) R.string.added_to_favorites_message
                                        else R.string.removed_from_favorites_message,
                                    ),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = if (track.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.add_to_favorites),
                            tint = if (track.favorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = { showPlaylistSheet = true },
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_to_playlist),
                        )
                    }
                }
            }
        }
    }

    if (showPlaylistSheet) {
        ModalBottomSheet(onDismissRequest = { showPlaylistSheet = false }) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.select_playlist),
                style = MaterialTheme.typography.titleMedium,
            )
            if (playlists.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.playlists_empty),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    items(playlists.size) { index ->
                        PlaylistRow(
                            playlist = playlists[index],
                            onClick = {
                                currentTrack?.let { trackForPlaylist ->
                                    coroutineScope.launch {
                                        playlistsAppViewModel.insertTrackToPlaylist(
                                            track = trackForPlaylist,
                                            playlistId = playlists[index].id,
                                        )
                                        currentTrack = trackForPlaylist.copy(playlistId = playlists[index].id)
                                        Toast.makeText(
                                            context,
                                            context.getString(
                                                R.string.added_to_playlist_message,
                                                playlists[index].name,
                                            ),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        showPlaylistSheet = false
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun parseDuration(trackTime: String): Int {
    return try {
        val parts = trackTime.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()
        (minutes * 60 + seconds) * 1000
    } catch (e: Exception) {
        0
    }
}

@Composable
private fun PlaylistRow(
    playlist: PlaylistApp,
    onClick: () -> Unit,
) {
    val coverModel = playlist.coverPath.takeIf { it.isNotBlank() }?.let(::File)
    val tracksText = pluralStringResource(
        id = R.plurals.tracks_count,
        count = playlist.tracks.size,
        playlist.tracks.size,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            if (coverModel == null) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        painter = painterResource(R.drawable.ic_music),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                AsyncImage(
                    model = coverModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = playlist.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = tracksText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "TrackApp details")
@Composable
private fun PlayerAppScreenPreview() {
    PlayerNavigationArgs.pendingTrack = TrackApp(
        id = 1L,
        trackName = "Billie Jean",
        artistName = "Michael Jackson",
        trackTime = "4:54",
        image = "",
        favorite = false,
        playlistId = 0L,
        previewUrl = "",
    )
    PlaylistMakerTheme(dynamicColor = false) {
        PlayerAppScreen(
            playlistsAppViewModel = PlaylistsAppViewModel(),
            navigateBack = {},
        )
    }
}