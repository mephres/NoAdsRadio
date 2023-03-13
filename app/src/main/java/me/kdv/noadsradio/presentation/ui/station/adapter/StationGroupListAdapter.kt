package com.intas.metrolog.presentation.ui.events.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import me.kdv.noadsradio.R
import me.kdv.noadsradio.domain.model.StationGroup
import me.kdv.noadsradio.presentation.ui.station.adapter.callback.StationGroupDiffCallback


class StationGroupListAdapter : ListAdapter<StationGroup, StationGroupViewHolder>(
    StationGroupDiffCallback()
) {

    lateinit var context: Context
    var onStationGroupClickListener: ((StationGroup, Int) -> Unit)? = null

    companion object {
        const val MAX_POOL_SIZE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationGroupViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.station_group_item, parent, false)
        context = parent.context
        return StationGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationGroupViewHolder, position: Int) {

        val stationGroup = getItem(position)

        if (stationGroup.isCurrent) {
            holder.stationGroupTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorAccent
                )
            )
        } else {
            holder.stationGroupTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.md_grey_500
                )
            )
        }
        holder.stationGroupTitle.text = stationGroup.description.uppercase()
        holder.stationGroupTitle.setOnClickListener {
            onStationGroupClickListener?.invoke(stationGroup, position)
        }
    }

    override fun onViewRecycled(holder: StationGroupViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}