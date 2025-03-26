package com.hellcorp.locationtrackerapp.location

import android.health.connect.datatypes.units.Velocity
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.osmdroid.util.Distance
import org.osmdroid.util.GeoPoint

@Parcelize
data class LocationModel(
    val velocity: Float,
    val distance: Float,
    val geoPointList: ArrayList<GeoPoint>,
    val bearing: Float = 0f,  // Direction in degrees
    val currentLocation: GeoPoint? = null
) : Parcelable
