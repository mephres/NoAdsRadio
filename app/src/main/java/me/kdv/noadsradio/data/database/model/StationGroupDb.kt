package me.kdv.noadsradio.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station_group")
data class StationGroupDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val isCurrent: Boolean
)
