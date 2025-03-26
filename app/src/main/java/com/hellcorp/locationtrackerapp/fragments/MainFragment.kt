package com.hellcorp.locationtrackerapp.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.hellcorp.locationtrackerapp.App
import com.hellcorp.locationtrackerapp.MainViewModel
import com.hellcorp.locationtrackerapp.R
import com.hellcorp.locationtrackerapp.data.ConverterDB
import com.hellcorp.locationtrackerapp.databinding.FragmentMainBinding
import com.hellcorp.locationtrackerapp.domain.TrackItem
import com.hellcorp.locationtrackerapp.location.LocationModel
import com.hellcorp.locationtrackerapp.location.LocationService
import com.hellcorp.locationtrackerapp.utils.DialogManager
import com.hellcorp.locationtrackerapp.utils.TimeUtils
import com.hellcorp.locationtrackerapp.utils.checkPermission
import com.hellcorp.locationtrackerapp.utils.showSnackbar
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {
    private var locationModel: LocationModel? = null
    private var polyline: Polyline? = null
    private var directionMarker: Marker? = null
    private var trackIsDrawned = false
    private var isServiceLocationEnabled = false
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var timer: Timer? = null
    private var startTime = 0L
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.VMFactory(
            (requireContext().applicationContext as App).database,
            ConverterDB()
        )
    }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setOsm()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Update both onResume() and onPause() like this:


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClickListener()
        checkServiceState()
        updateTimeTV()
        registerLocReciever()
        updateLocation()
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(broadcastReceiver)
    }

    private fun setOnClickListener() = with(binding) {
        val listener = onClickListener()
        btnCenter.setOnClickListener(listener)
        btnStartStopTrack.setOnClickListener(listener)
    }

    private fun onClickListener() = View.OnClickListener {
        when (it) {
            binding.btnCenter -> centerLocation()
            binding.btnStartStopTrack -> startStopService()
        }
    }

    private fun centerLocation() {
        binding.map.controller.animateTo(locationOverlay.myLocation)
        locationOverlay.enableFollowLocation()
    }

    private fun updateTimeTV() {
        viewModel.timeData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }
    }

    private fun updateLocation() = with(binding) {
        viewModel.locationUpdates.observe(viewLifecycleOwner) { model ->
            tvDistance.text = formatDistance(model.distance)
            tvCurrentVelocity.text =
                getString(R.string.speed, String.format("%.1f", model.velocity * 3.6))
            tvAverageVelocity.text = getString(R.string.average_speed, getAverageSpeed(model.distance))
            locationModel = model
            updatePolyline(model.geoPointList)
        }
    }

    private fun formatDistance(distance: Float): String {
        return if (distance < 1000f) {
            getString(R.string.distance_meter, String.format("%.1f", distance))
        } else {
            getString(R.string.distance_kilometer, String.format("%.1f", distance / 1000))
        }
    }

    private fun formatDistanceShortString(distance: Float): String {
        return if (distance < 1000f) {
            String.format("%.1f", distance) + " m"
        } else {
            String.format("%.1f", distance / 1000) + " km"
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    viewModel.timeData.value = getCurrentTime()
                }
            }
        }, 10, 10)
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun getCurrentTime(): String {
        return "Time: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

    private fun getAverageSpeed(distance: Float) = String.format(
        "%.1f",
        3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000f))
    )

    private fun getGeopointListString(points: List<GeoPoint>): String {
        val geoPointsString = StringBuilder()
        points.forEach {
            geoPointsString.append("${it.latitude};${it.longitude}/")
        }
        return geoPointsString.toString()
    }

    private fun startStopService() {
        if (isServiceLocationEnabled) {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_start_track_24)
            stopTimer()
            val trackItem = getTrackItem()
            DialogManager.showSaveTrackDialog(requireContext(),
                trackItem,
                binding.root,
                object : DialogManager.Listener {
                    override fun onClick() {
                        showSnackbar(binding.root, "Track saved", requireContext())
                        viewModel.saveTrackToDb(trackItem)
                    }
                })
        } else {
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_pause_track_24)
            startLocationService()
            LocationService.startTime = System.currentTimeMillis()
            startTimer()
        }
        isServiceLocationEnabled = !isServiceLocationEnabled
    }

    private fun getTrackItem(): TrackItem {
        return TrackItem(
            null,
            getCurrentTime(),
            TimeUtils.getCurrentDate(),
            formatDistanceShortString(locationModel?.distance ?: 0f),
            getAverageSpeed(locationModel?.distance ?: 0f) + " km/h",
            getGeopointListString(locationModel?.geoPointList ?: listOf())
        )
    }

    private fun startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
    }

    private fun checkServiceState() {
        isServiceLocationEnabled = LocationService.isRuning
        if (isServiceLocationEnabled) {
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_pause_track_24)
            startTimer()
        }
    }

    private fun setOsm() {
        Configuration.getInstance()
            .load(requireContext(), activity?.getSharedPreferences(OSM_KEY, Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
        binding.map.setMultiTouchControls(true)
        initOsm()
    }

    private fun initOsm() = with(binding) {
        polyline = Polyline().apply {
            try {
                outlinePaint.color = Color.parseColor(
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getString(SettingsFragment.COLOR_TRACK_KEY, "#FF0000FF")
                )
            } catch (e: Exception) {
                outlinePaint.color = Color.BLUE
            }
        }

        val locationProvider = GpsMyLocationProvider(activity).apply {
            setLocationUpdateMinTime(1000)
            setLocationUpdateMinDistance(1.0f)
        }

        locationOverlay = MyLocationNewOverlay(locationProvider, map).apply {
            enableMyLocation()
            enableFollowLocation()
        }

        locationOverlay.runOnFirstFix {
            activity?.runOnUiThread {
                map.overlays.clear()

                locationOverlay.myLocation?.let { currentLocation ->
                    directionMarker = Marker(map).apply {
                        val drawable = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_direction_arrow,
                            null
                        )
                        setIcon(drawable)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        position = currentLocation
                        setVisible(true)
                    }
                    map.overlays.addAll(listOf(locationOverlay, polyline, directionMarker))
                    map.controller.animateTo(currentLocation)
                    map.controller.setZoom(18.0)
                } ?: run {
                    Log.e("MainFragment", "Failed to get initial GPS location")
                }

                map.invalidate()
            }
        }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        activity?.runOnUiThread {
            try {
                locationModel?.let { model ->
                    model.currentLocation?.let { currentLoc ->
                        directionMarker?.position = currentLoc
                        directionMarker?.rotation = model.bearing
                        directionMarker?.setVisible(true)
                    }

                    if (list.isNotEmpty()) {
                        if (list.size > 1 && !trackIsDrawned) {
                            list.forEach { polyline?.addPoint(it) }
                            trackIsDrawned = true
                        } else {
                            polyline?.addPoint(list.last())
                        }
                    }
                    binding.map.invalidate()
                }
            } catch (e: Exception) {
                Log.e("MainFragment", "Update error: ${e.message}")
            }
        }
    }

    private fun registerPermissions() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    initOsm()
                } else {
                    showSnackbar(binding.map, "No location access permission!", requireContext())
                }
            }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkLocationPermissionVersionQPlus()
        } else {
            checkLocationPermissionBelowVersionQ()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkLocationPermissionVersionQPlus() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOsm()
            checkLocationEnabled()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkLocationPermissionBelowVersionQ() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOsm()
            checkLocationEnabled()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    private fun checkLocationEnabled() {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            DialogManager.showLocationEnableDialog(requireContext(),
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.LOCATION_MODEL_INTENT) {
                val locationModelJson = intent.getStringExtra(LocationService.LOCATION_MODEL_INTENT)
                val gson = Gson()
                val locationModel = gson.fromJson(locationModelJson, LocationModel::class.java)
                viewModel.locationUpdates.value = locationModel
            }
        }
    }

    private fun registerLocReciever() {
        val intentFilter = IntentFilter(LocationService.LOCATION_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(broadcastReceiver, intentFilter)
    }

    companion object {
        const val OSM_KEY = "osm_pref"

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}