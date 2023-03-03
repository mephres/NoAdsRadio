package me.kdv.noadsradio.data.repository

import me.kdv.noadsradio.data.database.dao.StationDao
import me.kdv.noadsradio.data.database.mapper.StationMapper
import me.kdv.noadsradio.data.network.model.StationDto
import me.kdv.noadsradio.domain.repository.StationRepository
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val stationMapper: StationMapper,
    private val stationDao: StationDao
): StationRepository {

    override suspend fun insertStationList(stations: List<StationDto>) {
        val dbList = stations.map {
            stationMapper.mapDtoToDb(it)
        }
        stationDao.insertStations(dbList)
    }

    override suspend fun deleteStations() {
        stationDao.deleteStations()
    }
}