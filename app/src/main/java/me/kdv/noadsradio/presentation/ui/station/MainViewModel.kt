package me.kdv.noadsradio.presentation.ui.station

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.kdv.noadsradio.domain.repository.StationGroupRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stationGroupRepository: StationGroupRepository
) : ViewModel() {

    val stationGroups = stationGroupRepository.getStationGroups().distinctUntilChanged()

    fun getInfo() {
        viewModelScope.launch {
            stationGroupRepository.getStationInfo()
        }
    }
}