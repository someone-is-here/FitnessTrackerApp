package com.example.fitnesstrackerapp.mvvm.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentProfileBinding
import com.example.fitnesstrackerapp.mvvm.MainActivity
import com.example.fitnesstrackerapp.mvvm.SigningActivity
import com.example.fitnesstrackerapp.mvvm.fragments.parent.Profile

class ProfileFragment : Profile() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var uid:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = firebaseAuth.currentUser?.uid.toString()

        setUpHandlers()
        setUpAnimation()
        setUserInfoFromDB()
        setUpUserPhoto(uid, binding.ivPhoto, binding.ivBackground)
    }

    private fun setUpAnimation() {
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.btnSettings.startAnimation(scaleAnimation)
        binding.llUserInfo.startAnimation(topToBottomAnimation)
        binding.tvBio.startAnimation(topToBottomAnimation)
        binding.llMain.startAnimation(topToBottomAnimation)
        binding.llSignOut.startAnimation(topToBottomAnimation)
    }

    private fun setUpHandlers() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_appSettingsFragment2)
        }
        binding.rlEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            removeUIDToSharedPref()

            val intent = Intent(context, SigningActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
    private fun setUserInfoFromDB(){
        val uid = firebaseAuth.currentUser!!.uid

        readCountriesFromDB{
            databaseReference.child("User").child(uid).child("Country").get().addOnSuccessListener {
                binding.tvCountry.text = countryList[it.value.toString()]
            }
        }

        databaseReference.child("User").child(uid).child("Username").get().addOnSuccessListener {
            binding.tvUsername.text = it.value.toString()
        }
        databaseReference.child("User").child(uid).child("Bio").get().addOnSuccessListener {
            binding.tvBio.text = it.value.toString()
        }
        databaseReference.child("User").child(uid).child("Followers").get().addOnSuccessListener {
            binding.tvFollowersNumber.text = it.value.toString()
        }
        databaseReference.child("User").child(uid).child("Following").get().addOnSuccessListener {
            binding.tvFollowingNumber.text = it.value.toString()
        }
    }

}