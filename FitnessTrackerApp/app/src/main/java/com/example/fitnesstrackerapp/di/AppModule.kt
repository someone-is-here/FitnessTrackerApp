package com.example.fitnesstrackerapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.fitnesstrackerapp.db.TrainingDatabase
import com.example.fitnesstrackerapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.fitnesstrackerapp.other.Constants.KEY_UID
import com.example.fitnesstrackerapp.other.Constants.RUNNING_DATABASE_NAME
import com.example.fitnesstrackerapp.other.Constants.SHARED_PREFERENCES_NAME
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

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true)

}