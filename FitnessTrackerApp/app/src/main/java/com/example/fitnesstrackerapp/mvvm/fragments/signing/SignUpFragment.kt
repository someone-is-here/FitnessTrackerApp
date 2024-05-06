package com.example.fitnesstrackerapp.mvvm.fragments.signing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSignUpBinding
import com.example.fitnesstrackerapp.mvvm.fragments.parent.Signing

class SignUpFragment : Signing() {
    private var binding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpHandlers()
        setAnimation(context)
    }
    override fun setAnimation(context: Context?){
        super.setAnimation(context)

        binding!!.tvSignUp.startAnimation(topToBottomAnimation)
        binding!!.btnSettings.startAnimation(settingsAnimation)

        binding!!.tvHaveAccount.startAnimation(scaleAnimation)
        binding!!.tvSignIn.startAnimation(scaleAnimation)
    }

    private fun setUpHandlers(){

        binding!!.btnSettings.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_appSettingsFragment);
        }

        binding!!.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding!!.btnSignUp.setOnClickListener{
            signUp()
        }
    }

    private fun checkInput(email:String, password:String, confirmPassword:String): Boolean {
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
        } else if (password.length < 6){
            binding!!.etPassword.error = requireContext().getString(R.string.password_6_symbols)
            return false
        } else if(password.isDigitsOnly()){
            binding!!.etPassword.error = requireContext().getString(R.string.password_1_character_or_symbol)
            return false
        }

        if (confirmPassword.isEmpty()){
            binding!!.etConfirmPassword.error = requireContext().getString(R.string.confirm_password_field_empty)
            return false
        }

        return true
    }

    private fun signUp(){
        val email = binding!!.etEmail.text.toString()
        val password = binding!!.etPassword.text.toString()
        val confirmPassword = binding!!.etConfirmPassword.text.toString()

        if(checkInput(email, password, confirmPassword)){
            if(password == confirmPassword){
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        it.result.credential?.let { it1 -> firebaseAuth.signInWithCredential(it1) }
                        updateUI(requireContext().getString(R.string.account_created))
                    } else {
                        Toast.makeText(activity, requireContext().getString(R.string.check_input_or_internet), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding!!.etPassword.error = requireContext().getString(R.string.passwords_different)
                binding!!.etConfirmPassword.error = requireContext().getString(R.string.passwords_different)
            }
        }
    }
}