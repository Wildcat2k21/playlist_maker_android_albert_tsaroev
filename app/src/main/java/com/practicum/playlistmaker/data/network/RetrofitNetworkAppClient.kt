package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.BaseAppResponse
import com.practicum.playlistmaker.data.dto.TrackSearchAppRequest
import retrofit2.HttpException
import java.io.IOException

class RetrofitNetworkAppClient(private val api: ITunesApiAppService) : NetworkAppClient {

    override suspend fun doRequest(dto: Any): BaseAppResponse {
        return try {
            when (dto) {
                is TrackSearchAppRequest -> api.searchTracks(
                    query = dto.expression,
                    media = "music",
                    entity = "song",
                    limit = 50,
                )

                else -> BaseAppResponse().apply {
                    resultCode = 400
                    errorMessage = "Invalid request type: expected TrackSearchAppRequest"
                }
            }
        } catch (e: HttpException) {
            BaseAppResponse().apply {
                resultCode = e.code()
                errorMessage = e.response()?.message()
                    ?: e.message
                    ?: "HTTP ${e.code()}"
            }
        } catch (e: IOException) {
            BaseAppResponse().apply {
                resultCode = -1
                errorMessage = "Network error: ${e.message ?: "Unknown IO error"}"
            }
        } catch (e: Exception) {
            BaseAppResponse().apply {
                resultCode = -2
                errorMessage = "Unexpected error: ${e.message ?: "Unknown error"}"
            }
        }
    }
}