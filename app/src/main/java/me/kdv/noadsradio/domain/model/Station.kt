package me.kdv.noadsradio.domain.model

data class Station(
    val id: Int,
    val stationId: Int,
    val groupId: Int,
    val name: String,
    val url: String,
    val noJingle: Boolean,
    val image: String,
    val smallTitle: String,
    var state: StationState = StationState.STOPPED,
    var position: Int = 0
)

enum class StationState{
    PLAYING,
    LOADED,
    STOPPED
}
