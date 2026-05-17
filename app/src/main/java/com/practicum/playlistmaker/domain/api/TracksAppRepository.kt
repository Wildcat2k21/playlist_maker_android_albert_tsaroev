package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.network.TrackApp
import kotlinx.coroutines.flow.Flow

interface TracksAppRepository {
    suspend fun searchTracks(expression: String): List<TrackApp>

    fun getAppTrackByNameAndArtist(track: TrackApp): Flow<TrackApp?>

    fun getAppFavoriteTracks(): Flow<List<TrackApp>>

    suspend fun insertTrackToPlaylist(track: TrackApp, playlistId: Long)

    suspend fun deleteTracksByPlaylistId(playlistId: Long)

    suspend fun deleteTrackFromPlaylist(track: TrackApp)

    suspend fun updateTrackFavoriteStatus(track: TrackApp, isFavorite: Boolean)
}