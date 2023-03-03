package me.kdv.noadsradio.data.database.mapper

import me.kdv.noadsradio.data.database.model.StationDb
import me.kdv.noadsradio.data.network.model.StationDto
import javax.inject.Inject

class StationMapper @Inject constructor() {
    fun mapDtoToDb(dto: StationDto): StationDb {
        return StationDb(
            id = dto.groupId * 1000 + dto.id,
            stationId = dto.id,
            groupId = dto.groupId,
            name = dto.name ?: noData,
            url = dto.url ?: "",
            noJingle = dto.noJingle
        )
    }

    companion object {
        const val noData = "Нет данных"
    }
}