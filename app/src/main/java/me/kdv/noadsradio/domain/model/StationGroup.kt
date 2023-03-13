package me.kdv.noadsradio.domain.model

data class StationGroup(
    val id: Int,
    val name: String,
    val description: String,
    var isCurrent: Boolean = false
)
