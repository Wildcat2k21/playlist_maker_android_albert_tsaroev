package com.practicum.playlistmaker.data.dto

class TracksSearchAppResponse(
    val resultCount: Int? = null,
    val results: List<TrackAppDto>? = null,
) : BaseAppResponse()