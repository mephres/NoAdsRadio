package me.kdv.noadsradio.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.kdv.noadsradio.data.database.dao.StationDao
import me.kdv.noadsradio.data.database.dao.StationGroupDao
import me.kdv.noadsradio.data.database.model.StationDb
import me.kdv.noadsradio.data.database.model.StationGroupDb

@Database(
    entities = [StationGroupDb::class, StationDb::class],
    version = 2, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var db: AppDatabase? = null
        private const val DB_NAME = "database.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }

                val instance =
                    Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    ).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .setJournalMode(JournalMode.TRUNCATE)
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun stationGroupDao(): StationGroupDao
    abstract fun stationDao(): StationDao
}