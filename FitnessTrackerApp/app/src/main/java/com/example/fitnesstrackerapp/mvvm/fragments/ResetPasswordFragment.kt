package com.example.fitnesstrackerapp.mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentResetPasswordBinding
import com.example.fitnesstrackerapp.other.Signing
import dagger.hilt.android.AndroidEntryPoint

class ResetPasswordFragment : Signing() {

    private var binding: FragmentResetPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers()
        setAnimation()
    }
    private fun setAnimation(){
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding!!.tvResetPassword.startAnimation(topToBottomAnimation)
        binding!!.tvSignUp.startAnimation(scaleAnimation)
        binding!!.tvSignIn.startAnimation(scaleAnimation)
    }
    private fun setUpHandlers() {

        binding!!.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_resetPasswordFragment_to_signInFragment)
        }

        binding!!.tvSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_resetPasswordFragment_to_signUpFragment)
        }

        binding!!.btnResetPassword.setOnClickListener {
            val email = binding!!.etEmail.text.toString()

            if(checkInput(email)) {
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(activity, "Email sent! PLease, check you email!", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_resetPasswordFragment_to_signInFragment)
                }.addOnFailureListener {
                    Toast.makeText(activity, "Check email or internet connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInput(email: String):Boolean {
        if (email.isEmpty()){
            binding!!.etEmail.error = "Email field is empty!"
            return false
        }else if(!isValidEmail(email)){
            binding!!.etEmail.error = "Invalid email!"
            return false
        }

        return true
    }
}