package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentAppSettingsBinding
import timber.log.Timber


class AppSettingsFragment : Fragment() {
    private lateinit var binding: FragmentAppSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers(savedInstanceState)
        setAnimation()
    }


    private fun setUpHandlers(savedInstanceState: Bundle?){
        binding.rlLanguageSwitch.setOnClickListener{
            findNavController().navigate(
                R.id.action_appSettingsFragment_to_languagesFragment
            )
        }
        binding.rlSentMessage.setOnClickListener{
            findNavController().navigate(
                R.id.action_appSettingsFragment_to_sendUsMessageFragment
            )
        }
        binding.tvEditProfile.setOnClickListener {
            findNavController().navigate(
                R.id.action_appSettingsFragment_to_editProfileFragment
            )
        }
//        requireActivity().supportFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.slide_in, // enter
//                R.anim.fade_out, // exit
//                R.anim.fade_in, // popEnter
//                R.anim.slide_out // popExit
//            )
//        }
        binding.icBack.setOnClickListener {
            // Delete from stack unnecessary fragments
            findNavController().popBackStack(R.id.languagesFragment,true)
            findNavController().popBackStack(R.id.sendUsMessageFragment,true)
            findNavController().popBackStack(R.id.editProfileFragment,true)

            //Getting destination
            val destination = findNavController().previousBackStackEntry!!.destination.id
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.appSettingsFragment, true).build()

                when (destination) {
                   R.id.welcomeFragment -> {
                       findNavController().navigate(
                           R.id.action_appSettingsFragment_to_welcomeFragment,
                           savedInstanceState,
                           navOptions)

                        // Clear stack (not to return to the app_settings & reduce doubled welcome fragment)
                       findNavController().popBackStack(R.id.appSettingsFragment,true)
                       findNavController().popBackStack(R.id.welcomeFragment,true)
                   }
                   R.id.signInFragment -> {
                       findNavController().navigate(R.id.action_appSettingsFragment_to_signInFragment,
                                                    savedInstanceState,
                                                    navOptions)
                       // Clear stack
                       findNavController().popBackStack(R.id.appSettingsFragment,true)
                       findNavController().popBackStack(R.id.signInFragment,true)
                   }
                    R.id.signUpFragment -> {
                        findNavController().navigate(R.id.action_appSettingsFragment_to_signUpFragment,
                            savedInstanceState,
                            navOptions)
                        // Clear stack
                        findNavController().popBackStack(R.id.appSettingsFragment,true)
                        findNavController().popBackStack(R.id.signUpFragment,true)

                    }

                    R.id.resetPasswordFragment -> {
                        findNavController().navigate(R.id.action_appSettingsFragment_to_resetPasswordFragment,
                            savedInstanceState,
                            navOptions)
                        // Clear stack
                        findNavController().popBackStack(R.id.appSettingsFragment,true)
                        findNavController().popBackStack(R.id.resetPasswordFragment,true)
                    }

                    else -> Timber.e("%s not found", destination)
                }

        }
    }
    private fun setAnimation(){
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.tvSettings.startAnimation(scaleAnimation)
        binding.llUserInfo.startAnimation(topToBottomAnimation)
        binding.llPersonalSettings.startAnimation(topToBottomAnimation)
        binding.llAppSettings.startAnimation(topToBottomAnimation)
        binding.llSignOut.startAnimation(topToBottomAnimation)
    }


}