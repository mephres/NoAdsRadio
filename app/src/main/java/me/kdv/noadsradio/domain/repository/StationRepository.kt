package me.kdv.noadsradio.domain.repository

import androidx.lifecycle.LiveData
import me.kdv.noadsradio.data.network.model.StationDto
import me.kdv.noadsradio.domain.model.Station

interface StationRepository {
    suspend fun insertStationList(stations: List<StationDto>)
    suspend fun deleteStations()
    suspend fun updateStation(station: Station)
    fun getStations(): LiveData<List<Station>>
    suspend fun resetAllStations()
    fun getStationByUrl(url: String): LiveData<Station>
}