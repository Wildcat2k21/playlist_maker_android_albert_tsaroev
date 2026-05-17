package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TracksSearchAppResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiAppService {

    @GET("search")
    suspend fun searchTracks(
        @Query("term") query: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): TracksSearchAppResponse
}