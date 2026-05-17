package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TrackAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppTrack(track: TrackAppEntity)

    @Query("SELECT * FROM tracks WHERE trackName = :trackName AND artistName = :artistName LIMIT 1")
    fun getAppTrackByNameAndArtist(trackName: String, artistName: String): Flow<TrackAppEntity?>

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getAppFavoriteTracks(): Flow<List<TrackAppEntity>>

    @Query("UPDATE tracks SET favorite = :isFavorite WHERE id = :trackId")
    suspend fun updateAppFavorite(trackId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM tracks WHERE id = :trackId LIMIT 1")
    suspend fun getAppTrackById(trackId: Long): TrackAppEntity?

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteAppTrackById(trackId: Long)
}