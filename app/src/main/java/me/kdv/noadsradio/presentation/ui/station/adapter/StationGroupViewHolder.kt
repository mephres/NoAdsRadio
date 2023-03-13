package com.intas.metrolog.presentation.ui.events.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.kdv.noadsradio.R

class StationGroupViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val stationGroupTitle = view.findViewById(R.id.stationGroupTitleTextView) as TextView
}