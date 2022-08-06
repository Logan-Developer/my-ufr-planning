package fr.loganH.myufrplanning.ui.overview

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.loganH.myufrplanning.data.repository.PlanningRepository
import fr.loganH.myufrplanning.model.PlanningItem
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SednaApiStatus {
    LOADING,
    ERROR,
    DONE
}

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val planningRepository: PlanningRepository
) : ViewModel() {

    // The internal MutableLiveData String that stores the most recent request
    private val _status = MutableLiveData<SednaApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<SednaApiStatus> = _status

    private val _planning = MutableLiveData<List<PlanningItem>>()

    val planning: LiveData<List<PlanningItem>> = _planning

    init {
        getPlanning(listOf(12962), 40)
    }

    private fun getPlanning(groupIds: List<Int>, nbDays: Int) {
        viewModelScope.launch {
            _status.value = SednaApiStatus.LOADING

            try {
                _planning.value = planningRepository.fetchPlanning(groupIds, nbDays)
                _status.value = SednaApiStatus.DONE
            }
            catch (e: Exception) {
                _status.value = SednaApiStatus.ERROR
                _planning.value = emptyList()
            }
        }
    }

    fun refresh() {
        getPlanning(listOf(12962), 40)
    }
}