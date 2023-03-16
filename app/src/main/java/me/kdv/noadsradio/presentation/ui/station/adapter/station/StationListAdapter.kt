package me.kdv.noadsradio.presentation.ui.station.adapter.station

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
import me.kdv.noadsradio.R
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.model.StationState
import me.kdv.noadsradio.presentation.ui.station.adapter.callback.StationDiffCallback

class StationListAdapter : ListAdapter<Station, StationViewHolder>(StationDiffCallback()) {

    lateinit var context: Context
    var onStationClickListener: ((Station, Int) -> Unit)? = null

    companion object {
        const val MAX_POOL_SIZE = 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.station_item,
                parent,
                false
            )
        context = parent.context
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {

        val station = getItem(position)

        holder.stationTitle.text = station.name
        holder.stationStyle.text = station.groupName

        drawStationState(holder = holder, station = station)
        drawStationLogo(holder = holder, station = station)

        holder.stationControlImageView.setOnClickListener {

            Glide.with(context)
                .load(ContextCompat.getDrawable(context, R.drawable.ic_circle_48))
                .centerCrop()
                .into(holder.stationControlImageView)

            notifyItemChanged(position)
            onStationClickListener?.invoke(station, position)
        }
    }

    private fun showProgressBar(holder: StationViewHolder, isShow: Boolean) {
        if (isShow) {
            holder.progressBar.visibility = View.VISIBLE
        } else {
            holder.progressBar.visibility = View.GONE
        }
    }

    private fun drawStationLogo(holder: StationViewHolder, station: Station) {
        holder.stationImageView.setImageDrawable(null)

        if (station.image.isNotEmpty()) {
            holder.stationSmallTitle.visibility = View.GONE

            val url = GlideUrl(
                station.image, LazyHeaders.Builder()
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(context))
                    .build()
            )
            Glide.with(context).load(url).centerCrop().into(holder.stationImageView)

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
                    R.color.md_grey_400
                )
            )
            holder.stationSmallTitle.visibility = View.VISIBLE
            holder.stationSmallTitle.text = station.smallTitle
        }
    }

    private fun drawStationState(holder: StationViewHolder, station: Station) {
        val drowable = when (station.state) {
            StationState.PLAYING -> {
                ContextCompat.getDrawable(context, R.drawable.ic_stop_circle_48)
            }
            StationState.LOADED -> {
                ContextCompat.getDrawable(context, R.drawable.ic_circle_48)
            }
            StationState.STOPPED -> {
                ContextCompat.getDrawable(context, R.drawable.ic_play_circle_48)
            }
        }
        Glide.with(context)
            .load(drowable)
            .centerCrop()
            .into(holder.stationControlImageView)

        if (station.state == StationState.LOADED) {
            showProgressBar(holder = holder, isShow = true)
        } else {
            showProgressBar(holder = holder, isShow = false)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}