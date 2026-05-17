package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.db.PlaylistAppDatabase
import com.practicum.playlistmaker.data.db.entity.PlaylistAppEntity
import com.practicum.playlistmaker.data.db.mapper.toAppDomain
import com.practicum.playlistmaker.domain.api.PlaylistsAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsAppRepositoryImpl(
    database: PlaylistAppDatabase,
) : PlaylistsAppRepository {
    private val playlistDao = database.playlistDao()

    override fun getPlaylist(playlistId: Long): Flow<PlaylistApp?> {
        return playlistDao.getAppPlaylistById(playlistId).map { it?.toAppDomain() }
    }

    override fun getAllAppPlaylists(): Flow<List<PlaylistApp>> {
        return playlistDao.getAllAppPlaylists().map { list -> list.map { it.toAppDomain() } }
    }

    override suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverPath: String,
        createdAt: Long,
    ) {
        playlistDao.insertAppPlaylist(
            PlaylistAppEntity(
                name = name,
                description = description,
                coverPath = coverPath,
                createdAt = createdAt,
            ),
        )
    }

    override suspend fun deleteAppPlaylistById(id: Long) {
        playlistDao.deleteAppPlaylistById(playlistId = id)
    }
}