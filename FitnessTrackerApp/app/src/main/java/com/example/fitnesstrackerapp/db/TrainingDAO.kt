package com.example.fitnesstrackerapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao // Database access object
interface TrainingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // replace old training with new one, when conflict appears
    suspend fun  insertTraining(training: Training)

    @Delete
    suspend fun deleteTraining(training: Training)

    @Query("select * from training order by timestamp DESC")
    fun getAllTrainingsSortedByDate(): LiveData<List<Training>>

    @Query("select * from training order by timeInMills DESC")
    fun getAllTrainingsSortedByTimeInMills(): LiveData<List<Training>>

    @Query("select * from training order by caloriesBurned DESC")
    fun getAllTrainingsSortedByCaloriesBurned(): LiveData<List<Training>>

    @Query("select * from training order by avgSpeedInKMH DESC")
    fun getAllTrainingsSortedByAvgSpeed(): LiveData<List<Training>>

    @Query("select * from training order by distanceInMeters DESC")
    fun getAllTrainingsSortedByDistance(): LiveData<List<Training>>

    @Query("select sum(timeInMills) from training")
    fun getTotalTimeInMilliseconds(): LiveData<List<Long>>

    @Query("select sum(caloriesBurned) from training")
    fun getTotalCaloriesBurned(): LiveData<List<Int>>

    @Query("select sum(distanceInMeters) from training")
    fun getTotalDistance(): LiveData<List<Int>>

    @Query("select avg(avgSpeedInKMH) from training")
    fun getTotalAverageSpeed(): LiveData<List<Float>>

}