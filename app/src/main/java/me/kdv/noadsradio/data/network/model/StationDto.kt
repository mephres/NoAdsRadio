package me.kdv.noadsradio.data.network.model

data class StationDto(
    var id: Int = 0,
    var groupId: Int = 0,
    val name: String? = null,
    val url: String? = null
)
