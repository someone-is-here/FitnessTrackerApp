package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentLanguagesBinding
import timber.log.Timber
import java.util.Locale


class LanguagesFragment : Fragment() {
    private lateinit var binding: FragmentLanguagesBinding
    private val languages = mapOf("English" to "en", "Беларуская" to "be", "Русский" to "ru")
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguagesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHandlers()
        setUpSpinner()
    }

    private fun setUpHandlers() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_languagesFragment_to_appSettingsFragment)
        }
    }

    private fun setUpSpinner() {
       val spinner = binding.spLanguageSwitch

        val lang = resources.configuration.locale.language
        //val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, languages.keys.toTypedArray())
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter

            val fullLangName = languages.filter { (key, value) -> value == lang }.keys

            // Set spinner position default
            val position = adapter.getPosition(fullLangName.first().capitalize())
            spinner.setSelection(position)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get full language name
                val language = parent?.getItemAtPosition(position).toString()
                //Get language code
                val langCode = languages.getValue(language)

                // Check if selected language equals app language
                if(langCode == resources.configuration.locale.language){
                    return
                }

                // Change language
                val locale = Locale(langCode)

                var config = resources.configuration
                config.locale = locale

                resources.updateConfiguration(config, resources.displayMetrics)

                progressDialog = ProgressDialog(requireContext())
                progressDialog!!.setMessage("Loading...")
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()

                // Update UI
                requireActivity().recreate()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (progressDialog != null){
            progressDialog!!.dismiss()
        }

    }

}

