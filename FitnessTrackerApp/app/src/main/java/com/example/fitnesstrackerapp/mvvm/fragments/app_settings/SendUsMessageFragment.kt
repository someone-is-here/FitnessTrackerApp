package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSendUsMessageBinding


class SendUsMessageFragment : Fragment() {
    private lateinit var binding:FragmentSendUsMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendUsMessageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers()
    }

    private fun setUpHandlers() {
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_languagesFragment_to_appSettingsFragment)
        }
    }
}