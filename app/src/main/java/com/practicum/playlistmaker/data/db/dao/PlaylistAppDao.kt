package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.practicum.playlistmaker.data.db.entity.PlaylistAppEntity
import com.practicum.playlistmaker.data.db.model.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistAppDao {
    @Insert
    suspend fun insertAppPlaylist(playlist: PlaylistAppEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deleteAppPlaylistById(playlistId: Long)

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllAppPlaylists(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    fun getAppPlaylistById(playlistId: Long): Flow<PlaylistWithTracks?>
}