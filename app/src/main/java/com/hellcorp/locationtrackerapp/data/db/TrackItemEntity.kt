package com.hellcorp.locationtrackerapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "track_item")
data class TrackItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "distance")
    val distance: String,
    @ColumnInfo(name = "average_speed")
    val averageSpeed: String,
    @ColumnInfo(name = "geopoint")
    val geopoint: String
)
