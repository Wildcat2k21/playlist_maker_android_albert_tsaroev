package com.practicum.playlistmaker.ui.playlist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.storage.PlaylistCoverAppStorage
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.utils.TopAppButtonBar

@Composable
fun CreatePlaylistAppScreen(
    modifier: Modifier = Modifier,
    playlistsAppViewModel: PlaylistsAppViewModel,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppButtonBar(
                context = context,
                text = stringResource(R.string.create_playlist_title),
                onClick = navigateBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        pickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val imageUri = selectedImageUri
                if (imageUri == null) {
                    Image(
                        painter = painterResource(R.drawable.add_playlist_photo),
                        contentDescription = stringResource(R.string.playlist_name),
                    )
                } else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = stringResource(R.string.playlist_name),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.playlist_name)) },
                singleLine = true,
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.playlist_description)) },
                minLines = 3,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val coverPath = selectedImageUri?.let { uri ->
                        PlaylistCoverAppStorage.copyToInternalStorage(context, uri).orEmpty()
                    }.orEmpty()
                    playlistsAppViewModel.createNewPlayList(
                        namePlaylist = name.trim(),
                        description = description.trim(),
                        coverPath = coverPath,
                        createdAt = System.currentTimeMillis(),
                    )
                    navigateBack()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.save_playlist))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePlaylistAppScreenPreview() {
    val fakeViewModel = remember { PlaylistsAppViewModel() }

    PlaylistMakerTheme(dynamicColor = false) {
        CreatePlaylistAppScreen(
            playlistsAppViewModel = fakeViewModel,
            navigateBack = {},
        )
    }
}