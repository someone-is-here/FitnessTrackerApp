package com.example.fitnesstrackerapp.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import com.example.fitnesstrackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
    val totalTimeRun = mainRepository.getTotalTimeInMilliseconds()
    val totalDistance = mainRepository.getTotalDistance()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    val totalAverageSpeed = mainRepository.getTotalAverageSpeed()

    val trainingSortedByDate = mainRepository.getAllTrainingsSortedByDate()
}