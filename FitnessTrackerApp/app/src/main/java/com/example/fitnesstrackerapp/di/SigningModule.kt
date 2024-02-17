//package com.example.fitnesstrackerapp.di
//
//import android.content.Context
//import android.content.Context.MODE_PRIVATE
//import android.content.SharedPreferences
//import com.example.fitnesstrackerapp.other.Constants.KEY_NAME
//import com.example.fitnesstrackerapp.other.Constants.SHARED_PREFERENCES_NAME
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//
//@Module
//@InstallIn(SingletonComponent::class)
//object SigningModule {
//
//    @Singleton
//    @Provides
//    fun provideSharedPreferences(
//        @ApplicationContext app: Context
//    ): SharedPreferences = app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
//
//    @Singleton
//    @Provides
//    fun provideEmail(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""
//
//}