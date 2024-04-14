package com.example.fitnesstrackerapp.mvvm.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentWelcomeBinding
import com.example.fitnesstrackerapp.mvvm.MainActivity
import com.example.fitnesstrackerapp.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private var binding: FragmentWelcomeBinding? = null

    @Inject
    lateinit var  sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    private fun isSignedIn(): Boolean {
        return sharedPref.contains(Constants.KEY_EMAIL)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if(isSignedIn()){
//            val intent = Intent(context, MainActivity::class.java)
//            startActivity(intent)
//            activity?.finish();
//        }

        setUpHandlers()

        // Must be last
        setAnimation()
    }

    private fun setAnimation(){
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding!!.tvWelcome.startAnimation(topToBottomAnimation)
        binding!!.tvToThe.startAnimation(topToBottomAnimation)
        binding!!.tvAppName.startAnimation(topToBottomAnimation)

        binding!!.btnLogin.startAnimation(scaleAnimation)
        binding!!.btnSignUp.startAnimation(scaleAnimation)
    }
    private fun setUpHandlers() {
        binding!!.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }

        binding!!.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
    }

}