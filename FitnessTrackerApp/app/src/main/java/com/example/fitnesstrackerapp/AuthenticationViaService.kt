package com.example.fitnesstrackerapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider


open class AuthenticationViaService: AppCompatActivity() {
    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var googleSignInClient: GoogleSignInClient

    private var authLog: String = "Authentication"
    private var providerTwitter: OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")
    private var providerGitHub: OAuthProvider.Builder = OAuthProvider.newBuilder("github.com")

    protected fun initGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    protected fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                signInGoogleAccount(account)
            }

        }else{
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                updateUI()
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
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
                    Log.e(authLog, "signInGitHubAccount:addOnFailureListener()")
                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(this, providerGitHub.build())
                .addOnSuccessListener {
                    updateUI()
                }
                .addOnFailureListener {
                    Log.e(authLog, "signInGitHubAccount(authentication):addOnFailureListener()")
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
                    Log.e(authLog, "signInTwitterAccount:addOnFailureListener()")
                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(this, providerTwitter.build())
                .addOnSuccessListener {
                    updateUI()
                }
                .addOnFailureListener {
                    Log.e(authLog, "signInTwitterAccount(authentication):addOnFailureListener()")
                }
        }
    }

    protected fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    protected fun updateUI(message: String = "Login successfully!") {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val launcher = registerForActivityResult( ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

}