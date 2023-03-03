package me.kdv.noadsradio.domain.repository

import androidx.lifecycle.LiveData
import me.kdv.noadsradio.domain.model.StationGroup

interface StationGroupRepository {
    suspend fun getStationInfo()
    fun getStationGroups(): LiveData<List<StationGroup>>
}