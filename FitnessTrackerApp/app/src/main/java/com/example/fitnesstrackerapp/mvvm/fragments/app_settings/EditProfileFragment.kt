package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentEditProfileBinding
import com.example.fitnesstrackerapp.databinding.NumberPitcherDialogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var bindingPicker: NumberPitcherDialogBinding
    private lateinit var database: DatabaseReference
    private lateinit var countryList: Map<String,String>
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference

        setDefaultValue()
        setAnimation()
        setCalendarValue()
        setUpHandlers()
        setUpLocationSpinner()
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
    private fun setDefaultValue() {
        binding.btnWeight.text = "${60} ${requireContext().getString(R.string.kg)}"
        binding.btnHeight.text = "${170} ${requireContext().getString(R.string.cm)}"
        binding.btnBirthday.text = "01.01.2000"
    }

    private fun setCalendarValue() {
        calendar.set(2000,1,1)
    }

    private fun setUpHandlers() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_appSettingsFragment)
        }
        setUpBirthdaySelector()
        setUpHeightSelector()
        setUpWeightSelector()
    }

    private fun setUpHeightSelector() {
        binding.btnHeight.setOnClickListener {
            val pickerDialog = Dialog(requireActivity())
            pickerDialog.setTitle("NumberPicker")
            pickerDialog.setContentView(R.layout.number_pitcher_dialog)

            bindingPicker = NumberPitcherDialogBinding.inflate(LayoutInflater.from(context));
            pickerDialog.setContentView(bindingPicker.getRoot())

            bindingPicker.tvHelp.text = requireContext().getString(R.string.cm)

            val numberPicker:NumberPicker = bindingPicker.npValue
            val value = getNumberFromHeight(binding.btnHeight.text.toString())

            numberPicker.minValue = 0
            numberPicker.maxValue = 250
            numberPicker.value = value
            numberPicker.wrapSelectorWheel = false

            bindingPicker.btnSet.setOnClickListener{
                binding.btnHeight.text = "${numberPicker.value} ${requireContext().getString(R.string.cm)}"
                pickerDialog.dismiss()
            }
            bindingPicker.btnCancel.setOnClickListener{
               pickerDialog.dismiss()
            }

            pickerDialog.show()
        }
    }
    private fun setUpWeightSelector() {
        binding.btnWeight.setOnClickListener {
            val pickerDialog = Dialog(requireActivity())
            pickerDialog.setTitle("NumberPicker")
            pickerDialog.setContentView(R.layout.number_pitcher_dialog)

            bindingPicker = NumberPitcherDialogBinding.inflate(LayoutInflater.from(context));
            pickerDialog.setContentView(bindingPicker.getRoot())

            bindingPicker.tvHelp.text = requireContext().getString(R.string.kg)

            val numberPicker:NumberPicker = bindingPicker.npValue
            val value = getNumberFromWeight(binding.btnWeight.text.toString())

            numberPicker.minValue = 0
            numberPicker.maxValue = 200
            numberPicker.value = value
            numberPicker.wrapSelectorWheel = false

            bindingPicker.btnSet.setOnClickListener{
                binding.btnWeight.text = "${numberPicker.value} ${requireContext().getString(R.string.kg)}"
                pickerDialog.dismiss()
            }
            bindingPicker.btnCancel.setOnClickListener{
                pickerDialog.dismiss()
            }

            pickerDialog.show()
        }
    }

    private fun getNumberFromWeight(str: String): Int {
        var num = str.replace(requireContext().getString(R.string.kg), "")
        num = num.replace(" ", "")

        var weight = 0

        try {
            weight = Integer.parseInt(num)
        } catch (_:Exception){ }

        return weight
    }

    private fun getNumberFromHeight(str:String):Int {
        var num = str.replace(requireContext().getString(R.string.cm), "")
        num = num.replace(" ", "")

        var height = 0

        try {
            height = Integer.parseInt(num)
        } catch (_:Exception){ }

        return height
    }

    private fun setUpBirthdaySelector() {
        binding.btnBirthday.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(), {DatePicker, year: Int, month: Int, day: Int ->
                    // Create a new Calendar instance to hold the selected date
                    val selectedDate = Calendar.getInstance()
                    calendar.set(year, month,day)
                    // Set the selected date using the values received from the DatePicker dialog
                    selectedDate.set(year, month, day)
                    //DateFormat("dd.MM.yyyy", binding.btnBirthday.text)
                    // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    // Format the selected date into a string
                    val formattedDate = dateFormat.format(selectedDate.time)
                    // Update the TextView to display the selected date with the "Selected Date: " prefix
                    binding.btnBirthday.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Show the DatePicker dialog
            datePickerDialog.show()
        }
    }

    private fun setUpLocationSpinner() {
        val spinner = binding.spLocation
        database.child("Country").get().addOnSuccessListener {
            val countries = it.value as HashMap<String, String>
            countryList = countries.toSortedMap()
            Timber.i("Got value ${it.value}")

            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_location_item, countryList.values.toTypedArray())
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(R.layout.spinner_location_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter

            try {
                var currentCountry:String? = null
                currentCountry = requireContext().resources.configuration.locale.country
                val position = adapter.getPosition(countryList.getValue(currentCountry))
                spinner.setSelection(position)
            } catch (_:Exception) {

            }


        }.addOnFailureListener{
            Toast.makeText(activity, requireContext().getString(R.string.check_input_or_internet),  Toast.LENGTH_SHORT).show()
            Timber.e( "Error getting data $it")
        }

    }
}