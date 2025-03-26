package com.hellcorp.locationtrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellcorp.locationtrackerapp.App
import com.hellcorp.locationtrackerapp.MainViewModel
import com.hellcorp.locationtrackerapp.data.ConverterDB
import com.hellcorp.locationtrackerapp.databinding.FragmentTracklistBinding
import com.hellcorp.locationtrackerapp.domain.TrackItem
import com.hellcorp.locationtrackerapp.fragments.adapters.TrackAdapter
import com.hellcorp.locationtrackerapp.utils.openFragment
import com.hellcorp.locationtrackerapp.utils.showSnackbar

class TrackListFragment : Fragment(), TrackAdapter.Listener {
    private var _bindng: FragmentTracklistBinding? = null
    private val binding get() = _bindng!!
    private lateinit var adapter: TrackAdapter
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
        _bindng = FragmentTracklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.trackList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        initAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindng = null
    }

    private fun initAdapter() = with(binding) {
        adapter = TrackAdapter(this@TrackListFragment)
        rvTracklist.layoutManager = LinearLayoutManager(requireContext())
        rvTracklist.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = TrackListFragment()
    }

    override fun onClick(track: TrackItem, type: TrackAdapter.ClickType) {
        if (type == TrackAdapter.ClickType.DELETE) {
            showSnackbar(binding.root, "Track successfuly deleted", requireContext())
            viewModel.removeTrackFromDb(track)
        } else {
            viewModel.setCurreantTrack(track)
            openFragment(ViewTrackFragment.newInstance())
        }
    }
}