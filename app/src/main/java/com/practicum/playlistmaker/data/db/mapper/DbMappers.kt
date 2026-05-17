package com.practicum.playlistmaker.data.db.mapper

import com.practicum.playlistmaker.data.db.entity.PlaylistAppEntity
import com.practicum.playlistmaker.data.db.entity.TrackAppEntity
import com.practicum.playlistmaker.data.db.model.PlaylistWithTracks
import com.practicum.playlistmaker.data.network.PlaylistApp
import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.data.utils.toAppHighResolutionArtwork

fun TrackApp.toAppEntity(): TrackAppEntity = TrackAppEntity(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    image = image,
    favorite = favorite,
    previewUrl = previewUrl,
)

fun TrackAppEntity.toAppDomain(): TrackApp = TrackApp(
    id = id,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    image = toAppHighResolutionArtwork(image),
    favorite = favorite,
    playlistId = 0L,
    previewUrl = previewUrl,
)

fun PlaylistAppEntity.toAppDomain(tracks: List<TrackApp>): PlaylistApp = PlaylistApp(
    id = id,
    name = name,
    description = description,
    tracks = tracks,
    coverPath = coverPath,
    createdAt = createdAt,
)

fun PlaylistWithTracks.toAppDomain(): PlaylistApp = playlist.toAppDomain(
    tracks = tracks.map { track ->
        track.toAppDomain().copy(playlistId = playlist.id)
    },
)