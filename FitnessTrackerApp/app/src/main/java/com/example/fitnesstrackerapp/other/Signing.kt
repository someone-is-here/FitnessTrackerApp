package com.example.fitnesstrackerapp.other

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.mvvm.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
open class Signing: Fragment() {
    @Inject
    protected lateinit var firebaseAuth: FirebaseAuth
    @Inject
    protected lateinit var googleSignInClient: GoogleSignInClient

    private val providerX = OAuthProvider.newBuilder("twitter.com")

    private val providerGitHub = OAuthProvider.newBuilder("github.com")

    protected fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                signInGoogleAccount(account)
            }

        }else{
            Toast.makeText(activity,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                updateUI()
            }else{
                Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected fun signInGitHubAccount(){
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    updateUI()
                }
                .addOnFailureListener {
                    Timber.d("signInGitHubAccount:addOnFailureListener()")
                }
        } else {
            activity?.let {
                firebaseAuth
                    .startActivityForSignInWithProvider(it, providerGitHub.build())
                    .addOnSuccessListener {
                        updateUI()
                    }
                    .addOnFailureListener {
                        Timber.d("signInGitHubAccount(authentication):addOnFailureListener()")
                    }
            }
        }

    }

    protected fun signInTwitterAccount() {
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    updateUI()
                }
                .addOnFailureListener {
                    Timber.d( "signInTwitterAccount:addOnFailureListener()")
                }
        } else {
            activity?.let {
                firebaseAuth
                    .startActivityForSignInWithProvider(it, providerX.build())
                    .addOnSuccessListener {
                        updateUI()
                    }
                    .addOnFailureListener {
                        Timber.d( "signInTwitterAccount(authentication):addOnFailureListener()")
                    }
            }
        }
    }

    protected fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    protected fun updateUI(message: String = "Login successfully!") {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private val launcher = registerForActivityResult( ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

}
