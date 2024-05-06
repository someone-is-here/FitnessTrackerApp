package com.example.fitnesstrackerapp.mvvm.fragments.parent

import android.app.DatePickerDialog
import android.app.Dialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.NumberPitcherDialogBinding
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
open class Profile: Fragment()  {
    private lateinit var bindingPicker: NumberPitcherDialogBinding
    @Inject
    lateinit var database: DatabaseReference
    private lateinit var countryList: Map<String,String>
    private val calendar = Calendar.getInstance()

    protected fun setUpEditProfileView(btnHeight: Button, btnWeight: Button, btnBirthday: Button, spLocation:Spinner){
        setDefaultValue(btnHeight, btnWeight, btnBirthday)
        setCalendarValue()
        setUpBirthdaySelector(btnBirthday)
        setUpHeightSelector(btnHeight)
        setUpWeightSelector(btnWeight)
        setUpLocationSpinner(spLocation)
    }
    private fun setDefaultValue(btnHeight: Button, btnWeight: Button, btnBirthday: Button) {
        btnWeight.text = "${60} ${requireContext().getString(R.string.kg)}"
        btnHeight.text = "${170} ${requireContext().getString(R.string.cm)}"
        btnBirthday.text = "01.01.2000"
    }

    private fun setCalendarValue() {
        calendar.set(2000,1,1)
    }
    private fun setUpHeightSelector(btnHeight: Button) {
        btnHeight.setOnClickListener {
            val pickerDialog = Dialog(requireActivity())
            pickerDialog.setTitle("NumberPicker")
            pickerDialog.setContentView(R.layout.number_pitcher_dialog)

            bindingPicker = NumberPitcherDialogBinding.inflate(LayoutInflater.from(context));
            pickerDialog.setContentView(bindingPicker.getRoot())

            bindingPicker.tvHelp.text = requireContext().getString(R.string.cm)

            val numberPicker: NumberPicker = bindingPicker.npValue
            val value = getNumberFromHeight(btnHeight.text.toString())

            numberPicker.minValue = 0
            numberPicker.maxValue = 250
            numberPicker.value = value
            numberPicker.wrapSelectorWheel = false

            bindingPicker.btnSet.setOnClickListener{
                btnHeight.text = "${numberPicker.value} ${requireContext().getString(R.string.cm)}"
                pickerDialog.dismiss()
            }
            bindingPicker.btnCancel.setOnClickListener{
                pickerDialog.dismiss()
            }

            pickerDialog.show()
        }
    }
    private fun setUpWeightSelector(btnWeight: Button) {
        btnWeight.setOnClickListener {
            val pickerDialog = Dialog(requireActivity())
            pickerDialog.setTitle("NumberPicker")
            pickerDialog.setContentView(R.layout.number_pitcher_dialog)

            bindingPicker = NumberPitcherDialogBinding.inflate(LayoutInflater.from(context));
            pickerDialog.setContentView(bindingPicker.getRoot())

            bindingPicker.tvHelp.text = requireContext().getString(R.string.kg)

            val numberPicker: NumberPicker = bindingPicker.npValue
            val value = getNumberFromWeight(btnWeight.text.toString())

            numberPicker.minValue = 0
            numberPicker.maxValue = 200
            numberPicker.value = value
            numberPicker.wrapSelectorWheel = false

            bindingPicker.btnSet.setOnClickListener{
                btnWeight.text = "${numberPicker.value} ${requireContext().getString(R.string.kg)}"
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
    private fun setUpBirthdaySelector(btnBirthday: Button) {
        btnBirthday.setOnClickListener{
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
                    btnBirthday.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            // Show the DatePicker dialog
            datePickerDialog.show()
        }
    }
    private fun setUpLocationSpinner(spLocation: Spinner) {
        val spinner = spLocation
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