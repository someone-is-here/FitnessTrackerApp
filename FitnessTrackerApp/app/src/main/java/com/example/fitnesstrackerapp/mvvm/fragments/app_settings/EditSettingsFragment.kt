package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentEditProfileBinding
import com.example.fitnesstrackerapp.mvvm.fragments.parent.Profile

class EditSettingsFragment : Profile() {
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAnimation()
        setUpHandlers()
        setUpEditProfileView(binding.etEmail,
                             binding.btnHeight,
                             binding.btnWeight,
                             binding.btnBirthday,
                             binding.spLocation)
    }

    private fun setAnimation(){
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.tvEditProfile.startAnimation(scaleAnimation)
        binding.llUserInfo.startAnimation(topToBottomAnimation)
        binding.etBio.startAnimation(topToBottomAnimation)
        binding.llEmail.startAnimation(topToBottomAnimation)
        binding.llCounty.startAnimation(topToBottomAnimation)
        binding.llBirthday.startAnimation(topToBottomAnimation)
        binding.llHeight.startAnimation(topToBottomAnimation)
        binding.llWeight.startAnimation(topToBottomAnimation)
        binding.btnSave.startAnimation(topToBottomAnimation)
    }

    private fun setUpHandlers() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_appSettingsFragment)
        }
    }
}