package me.kdv.noadsradio.presentation.ui.station.adapter.callback

import androidx.recyclerview.widget.DiffUtil
import me.kdv.noadsradio.domain.model.StationGroup

class StationGroupDiffCallback : DiffUtil.ItemCallback<StationGroup>() {
    override fun areItemsTheSame(oldItem: StationGroup, newItem: StationGroup): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StationGroup, newItem: StationGroup): Boolean {
        return oldItem == newItem
    }
}