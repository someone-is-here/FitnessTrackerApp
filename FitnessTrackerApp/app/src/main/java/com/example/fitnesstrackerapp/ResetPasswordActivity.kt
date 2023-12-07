package com.example.fitnesstrackerapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnesstrackerapp.databinding.ActivityResetPasswordBinding
import com.example.fitnesstrackerapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ResetPasswordActivity : AuthenticationViaService() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Initialize Google Authentication
        initGoogleSignIn()

        setUpHandlers()
    }

    private fun setUpHandlers() {
        binding.ivGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.ivGithub.setOnClickListener {
            signInGitHubAccount()
        }

        binding.ivTwitter.setOnClickListener {
            signInTwitterAccount()
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if(checkInput(email)) {
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(this, "Email sent! PLease, check you email!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }.addOnFailureListener {
                    Toast.makeText(this, "Check email or internet connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInput(email: String):Boolean {
        if (email.isEmpty()){
            binding.etEmail.error = "Email field is empty!"
            return false
        }else if(!isValidEmail(email)){
            binding.etEmail.error = "Invalid email!"
            return false
        }

        return true
    }
}