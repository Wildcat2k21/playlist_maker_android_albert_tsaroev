package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TrackSearchAppRequest
import com.practicum.playlistmaker.data.dto.TracksSearchAppResponse
import com.practicum.playlistmaker.data.dto.toAppDomainTracks
import com.practicum.playlistmaker.data.db.dao.PlaylistAppTrackDao
import com.practicum.playlistmaker.data.db.dao.TrackAppDao
import com.practicum.playlistmaker.data.db.entity.PlaylistAppTrackCrossRef
import com.practicum.playlistmaker.data.db.mapper.toAppDomain
import com.practicum.playlistmaker.data.db.mapper.toAppEntity
import com.practicum.playlistmaker.domain.api.TracksAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class TrackAppRepositoryImpl(
    private val networkClient: NetworkAppClient,
    private val trackDao: TrackAppDao,
    private val playlistTrackDao: PlaylistAppTrackDao,
) : TracksAppRepository {

    override suspend fun searchTracks(expression: String): List<TrackApp> {
        val q = expression.trim()
        if (q.isEmpty()) return emptyList()
        val response = networkClient.doRequest(TrackSearchAppRequest(q))
        if (response is TracksSearchAppResponse) {
            return response.toAppDomainTracks()
        }
        if (response.resultCode != 0) {
            throw IOException(
                response.errorMessage.ifEmpty { "Request failed with code ${response.resultCode}" },
            )
        }
        return emptyList()
    }

    override fun getAppTrackByNameAndArtist(track: TrackApp): Flow<TrackApp?> {
        return trackDao
            .getAppTrackByNameAndArtist(track.trackName, track.artistName)
            .map { it?.toAppDomain() }
    }

    override suspend fun insertTrackToPlaylist(track: TrackApp, playlistId: Long) {
        trackDao.upsertAppTrack(track.toAppEntity())
        playlistTrackDao.insertAppCrossRef(
            PlaylistAppTrackCrossRef(
                playlistId = playlistId,
                trackId = track.id,
            ),
        )
    }

    override suspend fun deleteTrackFromPlaylist(track: TrackApp) {
        if (track.playlistId > 0L) {
            playlistTrackDao.deleteAppCrossRef(playlistId = track.playlistId, trackId = track.id)
            deleteTrackIfUnused(track.id)
        } else {
            playlistTrackDao.deleteAppByTrackId(track.id)
            deleteTrackIfUnused(track.id)
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: TrackApp, isFavorite: Boolean) {
        val stored = trackDao.getAppTrackById(track.id)
        if (stored == null) {
            trackDao.upsertAppTrack(track.copy(favorite = isFavorite).toAppEntity())
        } else {
            trackDao.updateAppFavorite(trackId = track.id, isFavorite = isFavorite)
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        val trackIds = playlistTrackDao.getAppTrackIdsByPlaylistId(playlistId)
        playlistTrackDao.deleteAppByPlaylistId(playlistId)
        trackIds.forEach { trackId ->
            deleteTrackIfUnused(trackId)
        }
    }

    override fun getAppFavoriteTracks(): Flow<List<TrackApp>> {
        return trackDao.getAppFavoriteTracks().map { tracks -> tracks.map { it.toAppDomain() } }
    }

    private suspend fun deleteTrackIfUnused(trackId: Long) {
        val track = trackDao.getAppTrackById(trackId) ?: return
        if (playlistTrackDao.getAppTrackUsageCount(trackId) == 0 && !track.favorite) {
            trackDao.deleteAppTrackById(trackId)
        }
    }
}