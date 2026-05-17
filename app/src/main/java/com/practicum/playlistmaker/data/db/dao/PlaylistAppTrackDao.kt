package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.PlaylistAppTrackCrossRef

@Dao
interface PlaylistAppTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppCrossRef(crossRef: PlaylistAppTrackCrossRef)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAppByPlaylistId(playlistId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteAppCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun deleteAppByTrackId(trackId: Long)

    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getAppTrackUsageCount(trackId: Long): Int

    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getAppTrackIdsByPlaylistId(playlistId: Long): List<Long>
}