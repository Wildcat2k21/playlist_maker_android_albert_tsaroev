package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.practicum.playlistmaker.data.db.dao.PlaylistAppDao
import com.practicum.playlistmaker.data.db.dao.PlaylistAppTrackDao
import com.practicum.playlistmaker.data.db.dao.TrackAppDao
import com.practicum.playlistmaker.data.db.entity.PlaylistAppEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistAppTrackCrossRef
import com.practicum.playlistmaker.data.db.entity.TrackAppEntity

@Database(
    entities = [TrackAppEntity::class, PlaylistAppEntity::class, PlaylistAppTrackCrossRef::class],
    version = 4,
    exportSchema = false,
)
abstract class PlaylistAppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackAppDao
    abstract fun playlistDao(): PlaylistAppDao
    abstract fun playlistTrackDao(): PlaylistAppTrackDao

    companion object {
        val MIGRATION_3_TO_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tracks ADD COLUMN previewUrl TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}