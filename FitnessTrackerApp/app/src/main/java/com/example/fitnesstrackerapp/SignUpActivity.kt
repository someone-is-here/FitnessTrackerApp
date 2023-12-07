package com.example.fitnesstrackerapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.fitnesstrackerapp.databinding.ActivitySignUpBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AuthenticationViaService() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Authentication initialization
        firebaseAuth = FirebaseAuth.getInstance()

        //Initialize Google Authentication
        initGoogleSignIn()

        setUpHandlers()

    }

    private fun setUpHandlers(){
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

        binding.btnSignUpConfirm.setOnClickListener{
            signUp()
        }
    }

    private fun checkInput(email:String, password:String, confirmPassword:String): Boolean {
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
        } else if (password.length < 6){
            binding.etPassword.error = "Password should be at least 6 symbols!"
            return false
        } else if(password.isDigitsOnly()){
            binding.etPassword.error = "Password must contain at least 1 character or symbol!"
            return false
        }

        if (confirmPassword.isEmpty()){
            binding.etConfirmPassword.error = "Confirm password field is empty!"
            return false
        }

        return true
    }

    private fun signUp(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if(checkInput(email, password, confirmPassword)){
            if(password == confirmPassword){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        it.result.credential?.let { it1 -> firebaseAuth.signInWithCredential(it1) }
                        updateUI("Account created successfully!")
                    } else {
                        Toast.makeText(this, "Check input or internet connection!",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.etPassword.error = "Passwords are different!"
                binding.etConfirmPassword.error = "Passwords are different!"
            }
        }
    }

}