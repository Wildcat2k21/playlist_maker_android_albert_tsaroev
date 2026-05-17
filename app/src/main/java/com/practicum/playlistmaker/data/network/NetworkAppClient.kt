package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.BaseAppResponse

interface NetworkAppClient {
    suspend fun doRequest(dto: Any): BaseAppResponse
}