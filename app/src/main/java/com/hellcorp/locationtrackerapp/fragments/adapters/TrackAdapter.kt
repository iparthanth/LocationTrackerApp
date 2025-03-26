package com.hellcorp.locationtrackerapp.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hellcorp.locationtrackerapp.R
import com.hellcorp.locationtrackerapp.databinding.ItemTrackBinding
import com.hellcorp.locationtrackerapp.domain.TrackItem

class TrackAdapter(private val listener: Listener) :
    ListAdapter<TrackItem, TrackAdapter.TrackViewHolder>(Comparator) {
    class TrackViewHolder(view: View, private val listener: Listener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = ItemTrackBinding.bind(view)
        private var trackTemp: TrackItem? = null

        init {
            binding.ibDelete.setOnClickListener(this)
            binding.root.setOnClickListener(this)
        }

        fun bind(track: TrackItem) = with(binding) {
            trackTemp = track
            with(track) {
                tvDate.text = date
                tvTime.text = time
                tvDistance.text = distance
                tvSpeed.text = averageSpeed
            }
        }

        override fun onClick(view: View?) {
            val type = when (view) {
                binding.ibDelete -> ClickType.DELETE
                else -> ClickType.OPEN
            }
            trackTemp?.let { listener.onClick(it, type) }

        }
    }

    object Comparator : DiffUtil.ItemCallback<TrackItem>() {
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(track: TrackItem, type: ClickType)
    }

    enum class ClickType {
        DELETE,
        OPEN
    }
}
