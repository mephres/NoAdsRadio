package me.kdv.noadsradio.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import me.kdv.noadsradio.data.database.dao.StationDao
import me.kdv.noadsradio.data.database.mapper.StationMapper
import me.kdv.noadsradio.data.network.model.StationDto
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.repository.StationRepository
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val stationMapper: StationMapper,
    private val stationDao: StationDao
) : StationRepository {

    override suspend fun insertStationList(stations: List<StationDto>) {
        val dbList = stations.map {
            stationMapper.mapDtoToDb(it)
        }
        stationDao.insertStations(dbList)
    }

    override suspend fun deleteStations() {
        stationDao.deleteStations()
    }

    override suspend fun updateStation(station: Station) {
        val db = stationMapper.mapEntityToDb(station)
        stationDao.updateStation(db)
    }

    override fun getStations(): LiveData<List<Station>> {
        return stationDao.getStations().map {
            it.map {
                stationMapper.mapDbToEntity(it)
            }
        }
    }

    override suspend fun resetAllStations() {
        stationDao.resetAllStations()
    }

    override fun getStationByUrl(url: String): LiveData<Station> {
        return stationDao.getStationById(url).map {
            stationMapper.mapDbToEntity(it)
        }
    }
}