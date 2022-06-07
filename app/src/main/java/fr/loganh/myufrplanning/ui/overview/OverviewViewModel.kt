package fr.loganh.myufrplanning.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.loganh.myufrplanning.data.network.SednaAPI
import kotlinx.coroutines.launch

enum class SednaApiStatus {
    LOADING,
    SUCCESS,
    ERROR
}

class OverviewViewModel: ViewModel() {

    // The internal MutableLiveData String that stores the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status

    init {
        getPlanning(listOf(12986), 14)
    }

    private fun getPlanning(groupIds: List<Int>, nbDays: Int) {
        viewModelScope.launch {
            val result = SednaAPI.retrofitService.getPlanning(groupIds, nbDays)
            _status.value = result
        }
    }
}