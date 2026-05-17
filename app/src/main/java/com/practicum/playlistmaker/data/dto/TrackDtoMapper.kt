package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.data.network.TrackApp
import com.practicum.playlistmaker.data.utils.toAppHighResolutionArtwork

internal fun TrackAppDto.toAppDomainTrack(): TrackApp? {
    if (kind != null && kind != "song") return null
    val name = trackName?.trim().orEmpty()
    if (name.isEmpty()) return null
    val id = trackId ?: return null
    return TrackApp(
        id = id,
        trackName = name,
        artistName = artistName?.trim().orEmpty().ifEmpty { "—" },
        trackTime = formatTrackDurationMillis(trackTimeMillis),
        image = toAppHighResolutionArtwork(artworkUrl100),
        favorite = false,
        playlistId = 0L,
        previewUrl = previewUrl.orEmpty(),
    )
}

internal fun TracksSearchAppResponse.toAppDomainTracks(): List<TrackApp> {
    return results.orEmpty().mapNotNull { it.toAppDomainTrack() }
}

private fun formatTrackDurationMillis(millis: Long?): String {
    if (millis == null || millis <= 0L) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}