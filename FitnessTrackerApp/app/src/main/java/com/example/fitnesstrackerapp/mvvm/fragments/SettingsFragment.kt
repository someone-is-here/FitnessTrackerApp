package com.example.fitnesstrackerapp.mvvm.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldFromSharedPreferences()

        binding!!.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if(success){
                Snackbar.make(view, requireContext().getString(R.string.changes_saved), Snackbar.LENGTH_SHORT).show()
            } else {
              Snackbar.make(view, requireContext().getString(R.string.filled_out_fields), Snackbar.LENGTH_LONG).show()
            }
        }
    }
    private fun loadFieldFromSharedPreferences(){
//        val name = sharedPreferences.getString(KEY_NAME, "")
//        val height = sharedPreferences.getFloat(KEY_HEIGHT, 170f)
//        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)

//        binding!!.etUsername.setText(name)
//        binding!!.etHeight.setText(height.toString())
//        binding!!.etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val name = binding!!.etUsername.text.toString()
        val weight = binding!!.etWeight.text.toString()
        val height = binding!!.etHeight.text.toString()

        if(name.isEmpty() || weight.isEmpty() || height.isEmpty()){
            return false
        }
//        sharedPreferences.edit()
//            .putString(KEY_NAME, name)
//            .putFloat(KEY_WEIGHT, weight.toFloat())
//            .putFloat(KEY_HEIGHT, height.toFloat())
//            .apply()

        return true
    }

}