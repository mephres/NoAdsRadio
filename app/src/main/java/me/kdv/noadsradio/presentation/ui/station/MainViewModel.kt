package me.kdv.noadsradio.presentation.ui.station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val stationGroups = stationGroupRepository.getStationGroups()
    val stations = stationRepository.getStations()

    private var _currentMediaId : MutableLiveData<String> = MutableLiveData()
    val currentMediaId: LiveData<String>
    get() = _currentMediaId

    fun setCurrentMediaId(mediaId: String) {
        _currentMediaId.value = mediaId
    }

    fun getCurrentPlayingStation(url: String): LiveData<Station> {
        return stationRepository.getStationByUrl(url)
    }

    fun loadStationList() {
        viewModelScope.launch {
            stationGroupRepository.loadStationList()
        }
    }

    fun changeStationState(station: Station, state: StationState) {
        viewModelScope.launch {
            station.state = state
            stationRepository.updateStation(station)
        }
    }

    fun setIsCurrentStationGroup(stationGroup: Int) {

        viewModelScope.launch {
            stationGroups.value?.forEach {
                if (stationGroup == it.id) {
                    it.isCurrent = true
                } else {
                    it.isCurrent = false
                }
            }
            stationGroups.value?.let {
                stationGroupRepository.updateStationGroups(it)
            }
        }
    }

    fun resetAllStations() {
        viewModelScope.launch {
            stationRepository.resetAllStations()
        }
    }
}