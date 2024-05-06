package com.example.fitnesstrackerapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.other.Constants.KEY_EMAIL
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SigningModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext app: Context
    ) =
          GoogleSignIn.getClient(app, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(app, R.string.default_web_client_id))
            .requestEmail()
            .build())

    @Singleton
    @Provides
    fun provideEmail(sharedPref: SharedPreferences) = sharedPref.getString(KEY_EMAIL, "") ?: ""

    @Singleton
    @Provides
    fun provideFirebaseRealtime(): DatabaseReference = Firebase.database.reference

    @Singleton
    @Provides
    fun provideFirebaseStorage(): StorageReference = Firebase.storage.reference

}