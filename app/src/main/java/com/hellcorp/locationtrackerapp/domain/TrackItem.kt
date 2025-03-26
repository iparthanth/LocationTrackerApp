package com.hellcorp.locationtrackerapp.domain

data class TrackItem(
    val id: Int?,
    val time: String,
    val date: String,
    val distance: String,
    val averageSpeed: String,
    val geopoints: String
)
