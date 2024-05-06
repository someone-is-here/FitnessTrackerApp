package com.example.fitnesstrackerapp.mvvm.fragments.signing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSignInBinding
import com.example.fitnesstrackerapp.other.Constants.KEY_EMAIL
import com.example.fitnesstrackerapp.mvvm.fragments.parent.Signing
import javax.inject.Inject

class SignInFragment : Signing() {
    private var binding: FragmentSignInBinding? = null

    @set:Inject
    var email:String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHandlers()

        setAnimation(context)
    }
    override fun setAnimation(context: Context?) {
        super.setAnimation(context)

        val googleAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_google)
        val xAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_x)
        val githubAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_github)

        binding!!.tvLogin.startAnimation(topToBottomAnimation)
        binding!!.btnSettings.startAnimation(settingsAnimation)

        binding!!.btnGoogleSignIn.startAnimation(googleAnimation)
        binding!!.btnXSignIn.startAnimation(xAnimation)
        binding!!.btnGitHubSignIn.startAnimation(githubAnimation)

        binding!!.tvNoAccount.startAnimation(scaleAnimation)
        binding!!.tvSignUp.startAnimation(scaleAnimation)
    }

    private fun setUpHandlers() {
        binding!!.btnGoogleSignIn.setOnClickListener {
            signInGoogle()
        }

        binding!!.btnGitHubSignIn.setOnClickListener {
            signInGitHubAccount()
        }

        binding!!.btnXSignIn.setOnClickListener {
            signInTwitterAccount()
        }
        binding!!.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
        }

        binding!!.tvSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding!!.btnSettings.setOnClickListener{
            findNavController().navigate(R.id.action_signInFragment_to_appSettingsFragment);
        }

        binding!!.btnLogin.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val email = binding!!.etEmail.text.toString()
        val password = binding!!.etPassword.text.toString()

        if(checkInput(email, password)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    sharedPref.edit()
                        .putString(KEY_EMAIL, email)
                        .apply()
                    updateUI()
                } else {
                    binding!!.etEmail.error = requireContext().getString(R.string.invalid_credentials)
                    binding!!.etPassword.error = requireContext().getString(R.string.invalid_credentials)

                    Toast.makeText(activity, requireContext().getString(R.string.check_input_or_internet),  Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInput(email: String, password: String): Boolean {
        if (email.isEmpty()){
            binding!!.etEmail.error = requireContext().getString(R.string.email_field_empty)
            return false
        }else if(!isValidEmail(email)){
            binding!!.etEmail.error = requireContext().getString(R.string.email_invalid)
            return false
        }

        if (password.isEmpty()){
            binding!!.etPassword.error = requireContext().getString(R.string.password_field_empty)
            return false
        }

        return true
    }
}