package com.example.fitnesstrackerapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.fitnesstrackerapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AuthenticationViaService() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
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
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.btnLoginConfirm.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if(checkInput(email, password)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    updateUI()
                } else {
                    binding.etEmail.error = "Invalid credentials!"
                    binding.etPassword.error = "Invalid credentials!"

                    Toast.makeText(this, "Check input or internet connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInput(email: String, password: String): Boolean {
        if (email.isEmpty()){
            binding.etEmail.error = "Email field is empty!"
            return false
        }else if(!isValidEmail(email)){
            binding.etEmail.error = "Invalid email!"
            return false
        }

        if (password.isEmpty()){
            binding.etPassword.error = "Password field is empty!"
            return false
        }

        return true
    }
}