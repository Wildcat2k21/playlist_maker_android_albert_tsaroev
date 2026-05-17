package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.network.PlaylistApp
import kotlinx.coroutines.flow.Flow

interface PlaylistsAppRepository {
    fun getPlaylist(playlistId: Long): Flow<PlaylistApp?>

    fun getAllAppPlaylists(): Flow<List<PlaylistApp>>

    suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverPath: String,
        createdAt: Long,
    )

    suspend fun deleteAppPlaylistById(id: Long)
}