package com.practicum.playlistmaker.ui.playlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creator.DependencyProvider
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.domain.api.PlaylistsAppRepository
import com.practicum.playlistmaker.domain.api.TracksAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class PlaylistsAppViewModel(
    private val playlistsRepository: PlaylistsAppRepository,
    private val tracksRepository: TracksAppRepository,
) : ViewModel() {
    constructor() : this(
        playlistsRepository = object : PlaylistsAppRepository {
            override fun getPlaylist(playlistId: Long): Flow<PlaylistApp?> = flowOf(null)
            override fun getAllAppPlaylists(): Flow<List<PlaylistApp>> = flowOf(emptyList())
            override suspend fun addNewPlaylist(
                name: String,
                description: String,
                coverPath: String,
                createdAt: Long,
            ) = Unit
            override suspend fun deleteAppPlaylistById(id: Long) = Unit
        },
        tracksRepository = object : TracksAppRepository {
            override suspend fun searchTracks(expression: String): List<TrackApp> = emptyList()
            override fun getAppTrackByNameAndArtist(track: TrackApp): Flow<TrackApp?> = flowOf(null)
            override fun getAppFavoriteTracks(): Flow<List<TrackApp>> = flowOf(emptyList())
            override suspend fun insertTrackToPlaylist(track: TrackApp, playlistId: Long) = Unit
            override suspend fun deleteTracksByPlaylistId(playlistId: Long) = Unit
            override suspend fun deleteTrackFromPlaylist(track: TrackApp) = Unit
            override suspend fun updateTrackFavoriteStatus(track: TrackApp, isFavorite: Boolean) = Unit
        },
    )


    val playlists: Flow<List<PlaylistApp>> = playlistsRepository.getAllAppPlaylists()

    val favoriteList: Flow<List<TrackApp>> = tracksRepository.getAppFavoriteTracks()

    fun createNewPlayList(
        namePlaylist: String,
        description: String,
        coverPath: String,
        createdAt: Long,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(
                name = namePlaylist,
                description = description,
                coverPath = coverPath,
                createdAt = createdAt,
            )
        }
    }

    suspend fun insertTrackToPlaylist(track: TrackApp, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun toggleFavorite(track: TrackApp, isFavorite: Boolean) {
        tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
    }

    suspend fun deleteTrackFromPlaylist(track: TrackApp) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deleteAppPlaylistById(id: Long) {
        tracksRepository.deleteTracksByPlaylistId(id)
        playlistsRepository.deleteAppPlaylistById(id)
    }

    suspend fun isExist(track: TrackApp): TrackApp? {
        return tracksRepository.getAppTrackByNameAndArtist(track = track).firstOrNull()
    }

    companion object {
        fun appFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistsAppViewModel(
                        playlistsRepository = DependencyProvider.providePlaylistsRepository(context),
                        tracksRepository = DependencyProvider.provideTracksRepository(context),
                    ) as T
                }
            }
    }
}