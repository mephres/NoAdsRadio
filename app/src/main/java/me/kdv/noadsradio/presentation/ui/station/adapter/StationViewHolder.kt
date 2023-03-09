package com.intas.metrolog.presentation.ui.events.adapter

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import me.kdv.noadsradio.R

class StationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val stationTitle = view.findViewById(R.id.stationTitleTextView) as TextView
    val stationStyle = view.findViewById(R.id.stationStyleTextView) as TextView
    val stationSmallTitle = view.findViewById(R.id.stationSmallTitleTextView) as TextView
    val stationImageView = view.findViewById(R.id.stationImageView) as ShapeableImageView
    val stationControlImageView = view.findViewById(R.id.stationControlImageView) as ImageView
    val progressBar = view.findViewById(R.id.progressBar) as ProgressBar
}