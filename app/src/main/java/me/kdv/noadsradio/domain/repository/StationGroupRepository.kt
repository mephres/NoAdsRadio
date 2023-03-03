package me.kdv.noadsradio.domain.repository

interface StationGroupRepository {
    suspend fun getStationInfo()
}