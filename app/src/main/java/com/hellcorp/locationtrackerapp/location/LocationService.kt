package com.hellcorp.locationtrackerapp.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.hellcorp.locationtrackerapp.MainActivity
import com.hellcorp.locationtrackerapp.R
import com.hellcorp.locationtrackerapp.fragments.SettingsFragment
import org.osmdroid.util.GeoPoint

class LocationService : Service() {
    private var distance = 0.0f
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var lastLocation: Location? = null
    private var geoPointsList = ArrayList<GeoPoint>()

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            if (lastLocation != null && currentLocation != null) {
                if (currentLocation.speed > 0.3) { // Enabled for physical devices
                    distance += lastLocation!!.distanceTo(currentLocation)
                }
                geoPointsList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))

                val locationModule = LocationModel(
                    currentLocation.speed,
                    distance,
                    geoPointsList,
                    currentLocation.bearing,
                    GeoPoint(currentLocation.latitude, currentLocation.longitude)
                )
                sendLocationData(locationModule)
            }
            lastLocation = currentLocation
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        isRuning = true
        initLocation()
        startLocationUpdate()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRuning = false
        locationProvider.removeLocationUpdates(locationCallback)
    }

    private fun sendLocationData(locationModel: LocationModel) {
        val gson = Gson()
        val locationModelJson = gson.toJson(locationModel)
        val intent = Intent(LOCATION_MODEL_INTENT).apply {
            putExtra(LOCATION_MODEL_INTENT, locationModelJson)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            ).also { channel ->
                getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
            }
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 10, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracker Active")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(210, notification)
    }

    private fun initLocation() {
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun startLocationUpdate() {
        val updateIntervalMs = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(SettingsFragment.TIME_KEY, "1000")?.toLong() ?: 1000

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            updateIntervalMs
        ).apply {
            setMinUpdateIntervalMillis(500)
            setWaitForAccurateLocation(true)
        }.build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    companion object {
        const val LOCATION_MODEL_INTENT = "location_intent"
        const val CHANEL_ID = "tracker_channel_01"
        var isRuning = false
        var startTime = 0L
    }
}
