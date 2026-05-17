package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.data.network.TrackApp

interface TrackSearchAppInteractor {
    suspend fun searchTracks(expression: String): List<TrackApp>
}