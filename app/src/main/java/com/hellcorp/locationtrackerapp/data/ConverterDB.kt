package com.hellcorp.locationtrackerapp.data

import com.hellcorp.locationtrackerapp.data.db.TrackItemEntity
import com.hellcorp.locationtrackerapp.domain.TrackItem

class ConverterDB {
    fun map(track: TrackItem) = TrackItemEntity(
        id = track.id,
        time = track.time,
        date = track.date,
        distance = track.distance,
        averageSpeed = track.averageSpeed,
        geopoint = track.geopoints
    )

    fun map(track: TrackItemEntity) = TrackItem(
        id = track.id,
        time = track.time,
        date = track.date,
        distance = track.distance,
        averageSpeed = track.averageSpeed,
        geopoints = track.geopoint
    )
}