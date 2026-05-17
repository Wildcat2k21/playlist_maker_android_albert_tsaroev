package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TrackSearchAppInteractor
import com.practicum.playlistmaker.domain.api.TracksAppRepository
import com.practicum.playlistmaker.data.network.TrackApp


class TrackSearchInteractorImpl(private val repository: TracksAppRepository) : TrackSearchAppInteractor {

    override suspend fun searchTracks(expression: String): List<TrackApp> {
        return repository.searchTracks(expression)
    }
}