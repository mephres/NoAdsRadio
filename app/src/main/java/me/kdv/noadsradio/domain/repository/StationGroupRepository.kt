package me.kdv.noadsradio.domain.repository

import androidx.lifecycle.LiveData
import me.kdv.noadsradio.domain.model.StationGroup

interface StationGroupRepository {
    suspend fun loadStationList()
    fun getStationGroups(): LiveData<List<StationGroup>>
    suspend fun updateStationGroups(stationGroups: List<StationGroup>)
}