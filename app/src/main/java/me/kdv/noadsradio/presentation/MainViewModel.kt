package me.kdv.noadsradio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.kdv.noadsradio.domain.repository.StationGroupRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stationGroupRepository: StationGroupRepository
) : ViewModel() {

    fun getInfo() {
        viewModelScope.launch {
            stationGroupRepository.getStationInfo()
        }
    }
}