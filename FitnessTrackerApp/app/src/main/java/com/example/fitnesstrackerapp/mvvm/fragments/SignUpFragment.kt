package com.example.fitnesstrackerapp.mvvm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSignUpBinding
import com.example.fitnesstrackerapp.other.Signing
import dagger.hilt.android.AndroidEntryPoint

class SignUpFragment : Signing() {
    private var binding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers()
        setAnimation(context)
    }
    override fun setAnimation(context: Context?){
        super.setAnimation(context)

        binding!!.tvSignUp.startAnimation(topToBottomAnimation)
        binding!!.ivSettings.startAnimation(settingsAnimation)

        binding!!.tvHaveAccount.startAnimation(scaleAnimation)
        binding!!.tvSignIn.startAnimation(scaleAnimation)
    }

    private fun setUpHandlers(){
        binding!!.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding!!.btnSignUp.setOnClickListener{
            signUp()
        }
    }

    private fun checkInput(email:String, password:String, confirmPassword:String): Boolean {
        if (email.isEmpty()){
            binding!!.etEmail.error = "Email field is empty!"
            return false
        }else if(!isValidEmail(email)){
            binding!!.etEmail.error = "Invalid email!"
            return false
        }
        if (password.isEmpty()){
            binding!!.etPassword.error = "Password field is empty!"
            return false
        } else if (password.length < 6){
            binding!!.etPassword.error = "Password should be at least 6 symbols!"
            return false
        } else if(password.isDigitsOnly()){
            binding!!.etPassword.error = "Password must contain at least 1 character or symbol!"
            return false
        }

        if (confirmPassword.isEmpty()){
            binding!!.etConfirmPassword.error = "Confirm password field is empty!"
            return false
        }

        return true
    }

    private fun signUp(){
        val email = binding!!.etEmail.text.toString()
        val password = binding!!.etPassword.text.toString()
        val confirmPassword = binding!!.etConfirmPassword.text.toString()

        if(checkInput(email, password, confirmPassword)){
            if(password == confirmPassword){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        it.result.credential?.let { it1 -> firebaseAuth.signInWithCredential(it1) }
                        updateUI("Account created successfully!")
                    } else {
                        Toast.makeText(activity, "Check input or internet connection!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding!!.etPassword.error = "Passwords are different!"
                binding!!.etConfirmPassword.error = "Passwords are different!"
            }
        }
    }
}