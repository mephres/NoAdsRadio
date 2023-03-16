package me.kdv.noadsradio.presentation.ui.station.adapter.callback

import androidx.recyclerview.widget.DiffUtil
import me.kdv.noadsradio.domain.model.Station

class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
    override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
    }
}