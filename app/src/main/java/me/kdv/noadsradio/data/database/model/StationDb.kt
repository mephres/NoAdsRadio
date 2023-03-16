package me.kdv.noadsradio.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station")
data class StationDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val stationId: Int,
    val groupId: Int,
    val groupName: String,
    val name: String,
    val url: String,
    val noJingle: Boolean,
    val image: String,
    var state: Int
)
