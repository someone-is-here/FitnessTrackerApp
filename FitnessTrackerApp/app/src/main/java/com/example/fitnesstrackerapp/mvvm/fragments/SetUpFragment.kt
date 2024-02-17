package com.example.fitnesstrackerapp.mvvm.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSetUpBinding
import com.example.fitnesstrackerapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.fitnesstrackerapp.other.Constants.KEY_HEIGHT
import com.example.fitnesstrackerapp.other.Constants.KEY_NAME
import com.example.fitnesstrackerapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment() {
    @Inject
    lateinit var  sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen:Boolean = true

    private var binding: FragmentSetUpBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetUpBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setUpFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setUpFragment_to_trainingFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding!!.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success) {
                findNavController().navigate(R.id.action_setUpFragment_to_trainingFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean{
        val name = binding!!.etUsername.text.toString()
        val weight = binding!!.etWeight.text.toString()
        val height = binding!!.etHeight.text.toString()

        if(name.isEmpty() || weight.isEmpty() || height.isEmpty()){
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putFloat(KEY_HEIGHT, height.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        return true
    }
}