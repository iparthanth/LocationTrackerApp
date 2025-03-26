package com.hellcorp.locationtrackerapp.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.hellcorp.locationtrackerapp.App
import com.hellcorp.locationtrackerapp.MainViewModel
import com.hellcorp.locationtrackerapp.R
import com.hellcorp.locationtrackerapp.data.ConverterDB
import com.hellcorp.locationtrackerapp.databinding.FragmentViewTrackBinding
import com.hellcorp.locationtrackerapp.domain.TrackItem
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ViewTrackFragment : Fragment() {
    private var _bindng: FragmentViewTrackBinding? = null
    private val binding get() = _bindng!!
    private var startPosition: GeoPoint? = null
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.VMFactory(
            (requireContext().applicationContext as App).database,
            ConverterDB()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindng = FragmentViewTrackBinding.inflate(inflater, container, false)
        setOsm()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindng = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentTrack.observe(viewLifecycleOwner) {
            fetchData(it)
        }
        binding.btnCenter.setOnClickListener {
            binding.map.controller.animateTo(startPosition)
        }
    }

    private fun setOsm() {
        Configuration.getInstance()
            .load(
                requireContext(),
                activity?.getSharedPreferences(MainFragment.OSM_KEY, Context.MODE_PRIVATE)
            )
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME

        binding.map.setMultiTouchControls(true)
    }

    private fun fetchData(trackItem: TrackItem) = with(binding) {
        with(trackItem) {
            tvDate.text = date
            tvAverageVelocity.text = averageSpeed
            tvDistance.text = distance
            tvTime.text = time
        }
        val polyline = getPolyline(trackItem.geopoints)
        polyline.outlinePaint.color = Color.parseColor(
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(SettingsFragment.COLOR_TRACK_KEY, "FF000000")
        )
        map.overlays.add(polyline)
        setMarkers(polyline.actualPoints)
        goToStartPosition(polyline.actualPoints[0])
        startPosition = polyline.actualPoints[0]
    }

    private fun goToStartPosition(startPosition: GeoPoint) {
        binding.map.controller.zoomTo(16.0)
        binding.map.controller.animateTo(startPosition)
    }

    private fun setMarkers(list: List<GeoPoint>) = with(binding) {
        val startMarker = Marker(map)
        val finishMarker = Marker(map)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = getDrawable(requireContext(), R.drawable.ic_start)
        finishMarker.icon = getDrawable(requireContext(), R.drawable.ic_finish)
        startMarker.position = list[0]
        finishMarker.position = list.last()
        map.overlays.add(startMarker)
        map.overlays.add(finishMarker)
    }

    private fun getPolyline(geopoints: String): Polyline {
        val polyline = Polyline()
        val list = geopoints.split("/")
        list.forEach {
            if (it.isNotEmpty()) {
                val point = it.split(";")
                polyline.addPoint(GeoPoint(point[0].toDouble(), point[1].toDouble()))
            }
        }
        return polyline
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()
    }
}