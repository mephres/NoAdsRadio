package me.kdv.noadsradio.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.kdv.noadsradio.data.database.model.StationGroupDb

@Dao
interface StationGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<StationGroupDb>)

    @Query("SELECT * FROM station_group")
    fun getGroups(): LiveData<List<StationGroupDb>>
}