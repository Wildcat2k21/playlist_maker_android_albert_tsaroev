package com.practicum.playlistmaker.data.network

data class TrackApp(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val image: String,
    var favorite: Boolean,
    var playlistId: Long,
    val previewUrl: String = "",
)