package com.example.fitnesstrackerapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Training::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class TrainingDatabase: RoomDatabase() {
    abstract fun getTrainingDao(): TrainingDAO
}