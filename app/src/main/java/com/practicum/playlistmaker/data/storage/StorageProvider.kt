package com.practicum.playlistmaker.data.storage

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.practicum.playlistmaker.data.db.PlaylistAppDatabase

private val Context.searchHistoryDataStore by preferencesDataStore(name = "search_history")

object StorageProvider {
    @Volatile
    private var appDatabase: PlaylistAppDatabase? = null

    fun provideDatabase(context: Context): PlaylistAppDatabase {
        return appDatabase ?: synchronized(this) {
            appDatabase ?: Room.databaseBuilder(
                context.applicationContext,
                PlaylistAppDatabase::class.java,
                "playlist_maker.db",
            )
                .addMigrations(PlaylistAppDatabase.MIGRATION_3_TO_4)
                .fallbackToDestructiveMigration()
                .build()
                .also { appDatabase = it }
        }
    }

    fun provideSearchHistoryDataStore(context: Context) = context.applicationContext.searchHistoryDataStore
}