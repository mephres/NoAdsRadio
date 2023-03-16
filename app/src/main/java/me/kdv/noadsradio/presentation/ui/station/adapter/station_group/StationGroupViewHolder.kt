package me.kdv.noadsradio.presentation.ui.station.adapter.station_group

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.kdv.noadsradio.R

class StationGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val stationGroupTitle = view.findViewById(R.id.stationGroupTitleTextView) as TextView
}