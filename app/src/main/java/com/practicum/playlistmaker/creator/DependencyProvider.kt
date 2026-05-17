package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.network.ITunesApiAppFactory
import com.practicum.playlistmaker.data.network.PlaylistsAppRepositoryImpl
import com.practicum.playlistmaker.data.network.SearchHistoryAppRepositoryImpl
import com.practicum.playlistmaker.data.network.TrackAppRepositoryImpl
import com.practicum.playlistmaker.data.preferences.SearchHistoryAppPreferences
import com.practicum.playlistmaker.data.storage.StorageProvider
import com.practicum.playlistmaker.domain.api.PlaylistsAppRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryAppRepository
import com.practicum.playlistmaker.domain.api.TrackSearchAppInteractor
import com.practicum.playlistmaker.domain.api.TracksAppRepository
import com.practicum.playlistmaker.domain.impl.TrackSearchInteractorImpl

object DependencyProvider {
    fun provideTracksRepository(context: Context): TracksAppRepository {
        val localDatabase = StorageProvider.provideDatabase(context)
        return TrackAppRepositoryImpl(
            networkClient = ITunesApiAppFactory.networkClient(),
            trackDao = localDatabase.trackDao(),
            playlistTrackDao = localDatabase.playlistTrackDao(),
        )
    }

    fun providePlaylistsRepository(context: Context): PlaylistsAppRepository {
        return PlaylistsAppRepositoryImpl(
            database = StorageProvider.provideDatabase(context),
        )
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryAppRepository {
        val preferences = SearchHistoryAppPreferences(
            dataStore = StorageProvider.provideSearchHistoryDataStore(context),
        )
        return SearchHistoryAppRepositoryImpl(preferences = preferences)
    }

    fun provideTrackSearchInteractor(context: Context): TrackSearchAppInteractor {
        return TrackSearchInteractorImpl(provideTracksRepository(context))
    }
}