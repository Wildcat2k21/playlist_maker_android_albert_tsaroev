package com.practicum.playlistmaker.data.network

data class PlaylistApp(
    val id: Long = 0,
    val name: String,
    val description: String,
    var tracks: List<TrackApp>,
    val coverPath: String = "",
    val createdAt: Long = 0L,
)