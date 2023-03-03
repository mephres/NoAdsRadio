package me.kdv.noadsradio.data.network.model

data class StationGroupDto(
    val id: Int = 0,
    val name: String? = null,
    val description: String? = null,
    val stations: List<StationDto>? = null
)
