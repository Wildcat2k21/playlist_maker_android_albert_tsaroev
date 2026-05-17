package com.practicum.playlistmaker.data.db.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.practicum.playlistmaker.data.db.entity.PlaylistAppEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistAppTrackCrossRef
import com.practicum.playlistmaker.data.db.entity.TrackAppEntity

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistAppEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistAppTrackCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "trackId",
        ),
    )
    val tracks: List<TrackAppEntity>,
)