package com.example.fitnesstrackerapp.mvvm.fragments.parent

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Patterns
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.mvvm.MainActivity
import com.example.fitnesstrackerapp.other.Constants
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
    lateinit var  sharedPref: SharedPreferences
    @Inject
    protected lateinit var firebaseAuth: FirebaseAuth
    @Inject
    protected lateinit var googleSignInClient: GoogleSignInClient

    private val providerX = OAuthProvider.newBuilder("twitter.com")

    private val providerGitHub = OAuthProvider.newBuilder("github.com")

    protected lateinit var topToBottomAnimation:Animation
    protected lateinit var scaleAnimation:Animation
    protected lateinit var settingsAnimation: Animation

    protected fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    open fun setAnimation(context: Context?){
        topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)
        settingsAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_settings)
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                signInGoogleAccount(account)
            }

        }else{
            Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                sharedPref.edit()
                    .putString(Constants.KEY_EMAIL, account.email)
                    .apply()
                updateUI()
            }else{
                Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                Timber.e(it.exception.toString())
            }
        }
    }

    protected fun signInGitHubAccount(){
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    sharedPref.edit()
                        .putString(Constants.KEY_EMAIL, it.user?.email)
                        .apply()
                    updateUI()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    Timber.d("signInGitHubAccount:addOnFailureListener()")
                }
        } else {
            activity?.let { fragmentActivity ->
                firebaseAuth
                    .startActivityForSignInWithProvider(fragmentActivity, providerGitHub.build())
                    .addOnSuccessListener {
                        sharedPref.edit()
                            .putString(Constants.KEY_EMAIL, it.user?.email)
                            .apply()
                        updateUI()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
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
                    sharedPref.edit()
                        .putString(Constants.KEY_EMAIL, it.user?.email)
                        .apply()
                    updateUI()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    Timber.d( "signInTwitterAccount:addOnFailureListener()")
                }
        } else {
            activity?.let { fragmentActivity ->
                firebaseAuth
                    .startActivityForSignInWithProvider(fragmentActivity, providerX.build())
                    .addOnSuccessListener {
                        sharedPref.edit()
                            .putString(Constants.KEY_EMAIL, it.user?.email)
                            .apply()
                        updateUI()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        Timber.d( "signInTwitterAccount(authentication):addOnFailureListener()")
                    }
            }
        }
    }

    protected fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    protected fun updateUI(message: String = requireContext().getString(R.string.login_successfully)) {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private val launcher = registerForActivityResult( ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        } else {
            Toast.makeText(activity, requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            Timber.e(result.resultCode .toString() + result.data.toString())
        }
    }

}
