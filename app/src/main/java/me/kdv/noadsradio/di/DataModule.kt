package me.kdv.noadsradio.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.kdv.noadsradio.data.database.AppDatabase
import me.kdv.noadsradio.data.database.dao.StationDao
import me.kdv.noadsradio.data.database.dao.StationGroupDao
import me.kdv.noadsradio.data.database.mapper.StationGroupMapper
import me.kdv.noadsradio.data.database.mapper.StationMapper
import me.kdv.noadsradio.data.repository.StationGroupRepositoryImpl
import me.kdv.noadsradio.data.repository.StationRepositoryImpl
import me.kdv.noadsradio.domain.repository.StationGroupRepository
import me.kdv.noadsradio.domain.repository.StationRepository

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideStationGroupRepository(
        stationGroupMapper: StationGroupMapper,
        stationGroupDao: StationGroupDao,
        stationRepository: StationRepository
    ): StationGroupRepository {
        return StationGroupRepositoryImpl(
            stationGroupMapper = stationGroupMapper,
            stationGroupDao = stationGroupDao,
            stationRepository = stationRepository
        )
    }

    @Provides
    fun provideStationGroupDao(application: Application): StationGroupDao {
        return AppDatabase.getInstance(application).stationGroupDao()
    }

    @Provides
    fun provideStationRepository(
        stationMapper: StationMapper,
        stationDao: StationDao
    ): StationRepository {
        return StationRepositoryImpl(
            stationMapper = stationMapper,
            stationDao = stationDao
        )
    }

    @Provides
    fun provideStationDao(application: Application): StationDao {
        return AppDatabase.getInstance(application).stationDao()
    }
}