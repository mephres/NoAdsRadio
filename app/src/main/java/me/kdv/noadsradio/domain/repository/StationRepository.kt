package me.kdv.noadsradio.domain.repository

import me.kdv.noadsradio.data.network.model.StationDto

interface StationRepository {
    suspend fun insertStationList(stations: List<StationDto>)
    suspend fun deleteStations()
}