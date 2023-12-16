package com.example.fitnesstrackerapp.repositories

import com.example.fitnesstrackerapp.db.Training
import com.example.fitnesstrackerapp.db.TrainingDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val trainingDAO: TrainingDAO
) {
    suspend fun insertTraining(training: Training) = trainingDAO.insertTraining(training)
    suspend fun deleteTraining(training: Training) = trainingDAO.deleteTraining(training)

    fun getAllTrainingsSortedByDate() = trainingDAO.getAllTrainingsSortedByDate()
    fun getAllTrainingsSortedByDistance() = trainingDAO.getAllTrainingsSortedByDistance()
    fun getAllTrainingsSortedByTimeInMills() = trainingDAO.getAllTrainingsSortedByTimeInMills()
    fun getAllTrainingsSortedByAvgSpeed() = trainingDAO.getAllTrainingsSortedByAvgSpeed()
    fun getAllTrainingsSortedByCaloriesBurned() = trainingDAO.getAllTrainingsSortedByCaloriesBurned()
    fun getTotalAverageSpeed() = trainingDAO.getTotalAverageSpeed()
    fun getTotalCaloriesBurned() = trainingDAO.getTotalCaloriesBurned()
    fun getTotalDistance() = trainingDAO.getTotalDistance()
    fun getTotalTimeInMilliseconds() = trainingDAO.getTotalTimeInMilliseconds()


}