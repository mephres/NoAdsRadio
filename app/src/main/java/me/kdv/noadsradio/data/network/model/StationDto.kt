package me.kdv.noadsradio.data.network.model

import com.google.firebase.database.PropertyName

data class StationDto(
    var id: Int = 0,
    var groupId: Int = 0,
    var groupName: String? = null,
    val name: String? = null,
    val url: String? = null,
    @get:PropertyName("no_jingle")
    @set:PropertyName("no_jingle")
    var noJingle: Boolean = false,
    val image: String? = null
)
