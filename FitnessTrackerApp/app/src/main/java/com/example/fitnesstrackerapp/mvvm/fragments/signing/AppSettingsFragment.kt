package com.example.fitnesstrackerapp.mvvm.fragments.signing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentAppSettingsBinding
import com.example.fitnesstrackerapp.databinding.FragmentResetPasswordBinding


class AppSettingsFragment : Fragment() {
    private var binding: FragmentAppSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppSettingsBinding.inflate(inflater, container, false)

        return binding!!.root
    }


}