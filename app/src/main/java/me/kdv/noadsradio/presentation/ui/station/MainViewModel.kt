package me.kdv.noadsradio.presentation.ui.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.kdv.noadsradio.domain.model.Station
import me.kdv.noadsradio.domain.model.StationState
import me.kdv.noadsradio.domain.repository.StationGroupRepository
import me.kdv.noadsradio.domain.repository.StationRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stationGroupRepository: StationGroupRepository,
    private val stationRepository: StationRepository,
) : ViewModel() {

    val stationGroups = stationGroupRepository.getStationGroups().distinctUntilChanged()
    val stations = stationRepository.getStations().distinctUntilChanged()

    fun getInfo() {
        viewModelScope.launch {
            stationGroupRepository.getStationInfo()
        }
    }

    fun changeStationState(station: Station, state: StationState) {
        viewModelScope.launch {
            station.state = state
            stationRepository.updateStation(station)
        }
    }
}