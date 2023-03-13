package me.kdv.noadsradio.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.kdv.noadsradio.data.database.model.StationDb
import me.kdv.noadsradio.domain.model.StationState

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationDb>)

    @Query("SELECT * FROM station")
    fun getStations(): LiveData<List<StationDb>>

    @Query("DELETE FROM station")
    suspend fun deleteStations()

    @Update
    suspend fun updateStation(station: StationDb)

    @Query("UPDATE station SET state = :state")
    suspend fun resetAllStations(state: Int = StationState.STOPPED.ordinal)
}