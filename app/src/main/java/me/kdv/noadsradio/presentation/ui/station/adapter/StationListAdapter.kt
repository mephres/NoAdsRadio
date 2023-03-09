package com.intas.metrolog.presentation.ui.events.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.material.transition.MaterialFadeThrough
import me.kdv.noadsradio.R
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.model.StationState
import me.kdv.noadsradio.presentation.ui.station.adapter.callback.StationDiffCallback


class StationListAdapter : ListAdapter<Station, StationViewHolder>(StationDiffCallback()) {

    lateinit var context: Context
    var onStationClickListener: ((Station, Int) -> Unit)? = null

    val enterTransition = MaterialFadeThrough()
    val exitTransition = MaterialFadeThrough()

    companion object {
        const val MAX_POOL_SIZE = 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        context = parent.context
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {

        val station = getItem(position)

        holder.progressBar.visibility = View.GONE

        holder.stationTitle.text = station.name

        when(station.state) {
            StationState.PLAYING -> {
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.outline_stop_circle_48))
                    .centerCrop()
                    .into(holder.stationControlImageView)
            }
            StationState.LOADED -> {
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.outline_circle_48))
                    .centerCrop()
                    .into(holder.stationControlImageView)
                holder.progressBar.visibility = View.VISIBLE
            }
            StationState.STOPPED -> {
                Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.baseline_play_circle_outline_48))
                    .centerCrop()
                    .into(holder.stationControlImageView)
            }
        }

        holder.stationImageView.setImageDrawable(null)
        if (station.image.isNotEmpty()) {
            val url = GlideUrl(
                station.image, LazyHeaders.Builder()
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(context))
                    .build()
            )
            Glide.with(context).load(url).centerCrop().into(holder.stationImageView)
            holder.stationSmallTitle.visibility = View.GONE
            holder.stationImageView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.md_white
                )
            )
        } else {
            holder.stationImageView.setBackgroundResource(R.drawable.default_station_backgroung)
            holder.stationImageView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.md_blue_500
                )
            )
            holder.stationSmallTitle.visibility = View.VISIBLE
            holder.stationSmallTitle.text = station.smallTitle
        }
        holder.stationControlImageView.setOnClickListener {
            //station.state = StationState.PLAYING

            Glide.with(context)
                .load(ContextCompat.getDrawable(context, R.drawable.outline_circle_48))
                .centerCrop()
                .into(holder.stationControlImageView)

            notifyItemChanged(position)
            onStationClickListener?.invoke(station, position)
        }
    }

    override fun onViewRecycled(holder: StationViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}