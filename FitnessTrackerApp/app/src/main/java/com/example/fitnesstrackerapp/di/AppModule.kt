package com.example.fitnesstrackerapp.di

import android.content.Context
import androidx.room.Room
import com.example.fitnesstrackerapp.db.TrainingDatabase
import com.example.fitnesstrackerapp.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        TrainingDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideTrainingDAO(db: TrainingDatabase) = db.getTrainingDao()

}