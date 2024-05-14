package com.example.fitnesstrackerapp.mvvm.fragments.parent

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.NumberPitcherDialogBinding
import com.example.fitnesstrackerapp.other.Constants.KEY_UID
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
open class Profile: Fragment()  {
    private lateinit var bindingPicker: NumberPitcherDialogBinding

    protected lateinit var countryList: Map<String,String>
    private val calendar = Calendar.getInstance()

    @Inject
    lateinit var storageRef: StorageReference

    @Inject
    lateinit var databaseReference: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var  sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen:Boolean = true

    protected fun setUpEditProfileView(etEmail: EditText, btnHeight: Button, btnWeight: Button, btnBirthday: Button, spLocation:Spinner){
        setDefaultValue(etEmail,btnHeight, btnWeight, btnBirthday)
        setCalendarValue()
        setUpBirthdaySelector(btnBirthday)
        setUpHeightSelector(btnHeight)
        setUpWeightSelector(btnWeight)
        setUpLocationSpinner(spLocation)
    }
    private fun setDefaultValue(etEmail: EditText, btnHeight: Button, btnWeight: Button, btnBirthday: Button) {
        etEmail.setText(firebaseAuth.currentUser?.email ?: "")
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
    protected fun readCountriesFromDB(callback:() -> Unit) {
        databaseReference.child("Country").get().addOnSuccessListener {
            val countries = it.value as HashMap<String, String>
            countryList = countries.toSortedMap()
            Timber.i("Got value ${it.value}")
            callback()

        }.addOnFailureListener{
            Toast.makeText(activity, requireContext().getString(R.string.check_internet_connection),  Toast.LENGTH_SHORT).show()
            Timber.e( "Error getting data $it")
        }
    }
    protected fun setUpLocationSpinner(spLocation: Spinner, countryCode: String? = null) {
        readCountriesFromDB {
            val spinner = spLocation
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_location_item,
                countryList.values.toTypedArray()
            )
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(R.layout.spinner_location_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter

            try {
                var currentCountry: String
                if (countryCode == null) {
                    currentCountry = requireContext().resources.configuration.locale.country
                } else {
                    currentCountry = countryCode
                }
                val position = adapter.getPosition(countryList.getValue(currentCountry))
                spinner.setSelection(position)
            } catch (_: Exception) {

            }
        }

    }

    protected fun checkInput(username:String, birthday:String, height: String, weight: String): Boolean{
        if(username.isEmpty()){
            Toast.makeText(activity, requireContext().getString(R.string.username_required),  Toast.LENGTH_SHORT).show()
            return false
        }
        if(birthday.isEmpty()){
            Toast.makeText(activity, requireContext().getString(R.string.birthday_required),  Toast.LENGTH_SHORT).show()
            return false
        }
        if(height.isEmpty()){
            Toast.makeText(activity, requireContext().getString(R.string.height_required),  Toast.LENGTH_SHORT).show()
            return false
        }
        if(weight.isEmpty()){
            Toast.makeText(activity, requireContext().getString(R.string.weight_required),  Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    protected fun saveUser(username:String, bio:String, location:String, birthday:String, height:String, weight:String){
        val uid = firebaseAuth.currentUser!!.uid
        var countryCode:String? =null

        countryList.forEach { entry ->
            if (entry.value == location){
                countryCode = entry.key
            }
        }

        databaseReference.child("User").child(uid).setValue("Username")
        databaseReference.child("User").child(uid).setValue("Bio")
        databaseReference.child("User").child(uid).setValue("Country")
        databaseReference.child("User").child(uid).setValue("Birthday")
        databaseReference.child("User").child(uid).setValue("Height")
        databaseReference.child("User").child(uid).setValue("Weight")
        databaseReference.child("User").child(uid).setValue("Followers")
        databaseReference.child("User").child(uid).setValue("Following")

        databaseReference.child("User").child(uid).child("Username").setValue(username)
        databaseReference.child("User").child(uid).child("Bio").setValue(bio)
        databaseReference.child("User").child(uid).child("Country").setValue(countryCode)
        databaseReference.child("User").child(uid).child("Birthday").setValue(birthday)
        databaseReference.child("User").child(uid).child("Height").setValue(height)
        databaseReference.child("User").child(uid).child("Weight").setValue(weight)
        databaseReference.child("User").child(uid).child("Followers").setValue(0)
        databaseReference.child("User").child(uid).child("Following").setValue(0)
    }
    protected fun setUserInfo(etUsername: EditText,
                              etEmail: EditText,
                              etBio: EditText,
                              btnBirthday: Button,
                              spLocation:Spinner,
                              btnHeight: Button,
                              btnWeight: Button){

        val uid = firebaseAuth.currentUser!!.uid

        databaseReference.child("User").child(uid).child("Username").get().addOnSuccessListener {
            etUsername.setText(it.value.toString())
        }
        val email = firebaseAuth.currentUser!!.email
        etEmail.setText(email)

        databaseReference.child("User").child(uid).child("Bio").get().addOnSuccessListener {
            etBio.setText(it.value.toString())
        }
        databaseReference.child("User").child(uid).child("Country").get().addOnSuccessListener {
            setUpLocationSpinner(spLocation, it.value.toString())
        }
        databaseReference.child("User").child(uid).child("Birthday").get().addOnSuccessListener {
            btnBirthday.text = it.value.toString()
        }
        databaseReference.child("User").child(uid).child("Height").get().addOnSuccessListener {
            btnHeight.text = it.value.toString()
        }
        databaseReference.child("User").child(uid).child("Weight").get().addOnSuccessListener {
            btnWeight.text = it.value.toString()
        }
    }
    protected fun setUpUserPhoto(uid:String, ivPhoto: CircleImageView, ivBackground: LinearLayout){
        val profileRef = storageRef.child("images/${uid}/${"profile.jpg"}")

        val localFileProfile = File.createTempFile("profile", "jpg")

        profileRef.getFile(localFileProfile).addOnSuccessListener {
            ivPhoto.setImageURI(Uri.fromFile(localFileProfile))
        }.addOnFailureListener {
            Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }

        val backgroundRef = storageRef.child("images/${uid}/${"background.jpg"}")

        val localFileBackground = File.createTempFile("background", "jpg")

        backgroundRef.getFile(localFileBackground).addOnSuccessListener {
            ivBackground.background = Drawable.createFromPath(localFileBackground.absolutePath)
        }.addOnFailureListener {
            Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    protected fun writeUIDToSharedPref(){

        val uid = firebaseAuth.currentUser!!.uid

        sharedPref.edit()
            .putString(KEY_UID, uid)
            .apply()
    }
    protected fun removeUIDToSharedPref(){
        sharedPref.edit()
            .remove(KEY_UID)
            .apply()
    }
    protected fun checkUserExists(callback:(Boolean) -> Unit) {
        val uid = firebaseAuth.currentUser!!.uid
        try {
            databaseReference.child("User").child(uid).get().addOnSuccessListener {
                if(it.value != null){
                    callback(true)
                } else {
                    callback(false)
                }
            }.addOnFailureListener{
                callback(false)
            }
        }catch (ex:Exception){
            callback(false)
        }
    }
}