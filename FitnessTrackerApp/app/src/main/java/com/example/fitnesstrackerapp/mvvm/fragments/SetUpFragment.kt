package com.example.fitnesstrackerapp.mvvm.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSetUpBinding
import com.example.fitnesstrackerapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.fitnesstrackerapp.other.Constants.KEY_HEIGHT
import com.example.fitnesstrackerapp.other.Constants.KEY_NAME
import com.example.fitnesstrackerapp.other.Constants.KEY_WEIGHT
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.net.URI
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment() {
    @Inject
    lateinit var  sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen:Boolean = true

    @Inject
    lateinit var storageRef: StorageReference

    @Inject
    lateinit var databaseReference: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentSetUpBinding
    private lateinit var uid:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = firebaseAuth.currentUser?.uid.toString()

        setUpHandlers()
//        if(!isFirstAppOpen){
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(R.id.setUpFragment, true)
//                .build()
//            findNavController().navigate(
//                R.id.action_setUpFragment_to_trainingFragment,
//                savedInstanceState,
//                navOptions
//            )
//        }

//        binding!!.tvContinue.setOnClickListener {
//            val success = writePersonalDataToSharedPref()
//            if(success) {
//                findNavController().navigate(R.id.action_setUpFragment_to_trainingFragment)
//            } else {
//                Snackbar.make(requireView(), requireContext().getString(R.string.filled_out_fields), Snackbar.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun setUpHandlers() {
        binding.btnUploadBackground.setOnClickListener {
            val metrics = requireContext().resources.displayMetrics
            val dpWidth = metrics.widthPixels / metrics.density - 100
            val dpHeight = metrics.heightPixels / metrics.density

            ImagePicker.with(this)
                .crop(dpHeight,dpWidth)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForBackgroundImageResult.launch(intent)
                }
        }
        binding.btnUploadProfile.setOnClickListener {
            ImagePicker.with(this)
                .crop(1f,1f)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

    }
    private val startForBackgroundImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

//                val filePath = fileUri.toFile()
//
//                val drawable = Drawable.createFromPath(filePath.absolutePath)

                binding.ivBackground.setImageURI(fileUri)

                val backgroundRef = storageRef.child("background/${uid}/${fileUri.lastPathSegment}")
                val uploadTask = backgroundRef.putFile(fileUri)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    Toast.makeText(requireContext(),  requireContext().getString(R.string.somethig_went_wrong), Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.upload_successful), Toast.LENGTH_SHORT).show()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),  requireContext().getString(R.string.somethig_went_wrong), Toast.LENGTH_SHORT).show()
            }
        }
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

//                val filePath = fileUri.toFile()
//
//                val drawable = Drawable.createFromPath(filePath.absolutePath)
                binding.ivProfile.setImageURI(fileUri)

                val profileRef = storageRef.child("profile/${uid}/${fileUri.lastPathSegment}")
                val uploadTask = profileRef.putFile(fileUri)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    Toast.makeText(requireContext(),  requireContext().getString(R.string.somethig_went_wrong), Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.upload_successful), Toast.LENGTH_SHORT).show()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),  requireContext().getString(R.string.somethig_went_wrong), Toast.LENGTH_SHORT).show()
            }
        }

    private fun writePersonalDataToSharedPref(): Boolean{
//        val name = binding!!.etUsername.text.toString()
//        val weight = binding!!.etWeight.text.toString()
//        val height = binding!!.etHeight.text.toString()
//
//        if(name.isEmpty() || weight.isEmpty() || height.isEmpty()){
//            return false
//        }
//
//        sharedPref.edit()
//            .putString(KEY_NAME, name)
//            .putFloat(KEY_WEIGHT, weight.toFloat())
//            .putFloat(KEY_HEIGHT, height.toFloat())
//            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
//            .apply()
//
        return true
    }
}