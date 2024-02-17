package com.example.fitnesstrackerapp.mvvm.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstrackerapp.db.Training
import com.example.fitnesstrackerapp.other.SortType
import com.example.fitnesstrackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val trainingSortedByDate = mainRepository.getAllTrainingsSortedByDate()
    private val trainingSortedByDistance = mainRepository.getAllTrainingsSortedByDistance()
    private val trainingSortedByAvgSpeed = mainRepository.getAllTrainingsSortedByAvgSpeed()
    private val trainingSortedByTimeInMills = mainRepository.getAllTrainingsSortedByTimeInMills()
    private val trainingSortedByCaloriesBurned = mainRepository.getAllTrainingsSortedByCaloriesBurned()

    val trainings = MediatorLiveData<List<Training>>()
    var sortType = SortType.DATE

    init {
        trainings.addSource(trainingSortedByDate){ result ->
            if(sortType == SortType.DATE){
                result?.let { trainings.value = it }
            }
        }
        trainings.addSource(trainingSortedByAvgSpeed){ result ->
            if(sortType == SortType.AVG_SPEED){
                result?.let { trainings.value = it }
            }
        }
        trainings.addSource(trainingSortedByDistance){ result ->
            if(sortType == SortType.DISTANCE){
                result?.let { trainings.value = it }
            }
        }
        trainings.addSource(trainingSortedByTimeInMills){ result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let { trainings.value = it }
            }
        }
        trainings.addSource(trainingSortedByCaloriesBurned){ result ->
            if(sortType == SortType.CALORIES_BURNED){
                result?.let {
                    trainings.value = it }
            }
        }
    }
    fun sortRuns(sortType: SortType) = when(sortType) {
        SortType.DATE -> trainingSortedByDate.value?.let{ trainings.value = it }
        SortType.AVG_SPEED -> trainingSortedByAvgSpeed.value?.let{ trainings.value = it }
        SortType.RUNNING_TIME -> trainingSortedByTimeInMills.value?.let{ trainings.value = it }
        SortType.CALORIES_BURNED -> trainingSortedByCaloriesBurned.value?.let{ trainings.value = it }
        SortType.DISTANCE -> trainingSortedByDistance.value?.let{ trainings.value = it }
    }.also {
        this.sortType = sortType
    }
    fun insertTraining(training: Training) = viewModelScope.launch {
        mainRepository.insertTraining(training)
    }
}