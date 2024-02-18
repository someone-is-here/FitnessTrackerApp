package com.example.fitnesstrackerapp.mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSignInBinding
import com.example.fitnesstrackerapp.other.Signing
import dagger.hilt.android.AndroidEntryPoint

class SignInFragment : Signing() {
    private var binding: FragmentSignInBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers()
    }
    private fun setUpHandlers() {
        binding!!.ivGoogle.setOnClickListener {
            signInGoogle()
        }

        binding!!.ivGithub.setOnClickListener {
            signInGitHubAccount()
        }

        binding!!.ivTwitter.setOnClickListener {
            signInTwitterAccount()
        }
        binding!!.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
        }

        binding!!.btnSignup.setOnClickListener{
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding!!.btnLoginConfirm.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val email = binding!!.etEmail.text.toString()
        val password = binding!!.etPassword.text.toString()

        if(checkInput(email, password)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    updateUI()
                } else {
                    binding!!.etEmail.error = "Invalid credentials!"
                    binding!!.etPassword.error = "Invalid credentials!"

                    Toast.makeText(activity, "Check input or internet connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInput(email: String, password: String): Boolean {
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
        }

        return true
    }
}