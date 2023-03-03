package me.kdv.noadsradio.data.database.mapper

import me.kdv.noadsradio.data.database.model.StationGroupDb
import me.kdv.noadsradio.data.network.model.StationGroupDto
import me.kdv.noadsradio.domain.model.StationGroup
import javax.inject.Inject

class StationGroupMapper @Inject constructor() {
    fun mapDtoToDb(dto: StationGroupDto): StationGroupDb {
        return StationGroupDb(
            id = dto.id,
            name = dto.name ?: noData,
            description = dto.description ?: noData
        )
    }

    fun mapDbToEntity(db: StationGroupDb): StationGroup {
        return StationGroup(
            id = db.id,
            name = db.name,
            description = db.description
        )
    }

    companion object {
        const val noData = "Нет данных"
    }
}