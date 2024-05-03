package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

}