package com.example.fitnesstrackerapp.mvvm.fragments.app_settings

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentCreateAccountBinding
import com.example.fitnesstrackerapp.mvvm.MainActivity
import com.example.fitnesstrackerapp.mvvm.fragments.parent.Profile
import com.github.dhaval2404.imagepicker.ImagePicker

class CreateAnAccountFragment : Profile() {
    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var uid:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = firebaseAuth.currentUser?.uid.toString()

        setAnimation()
        setUpHandlers()
        setUpEditProfileView(binding.etEmail,
                             binding.btnHeight,
                             binding.btnWeight,
                             binding.btnBirthday,
                             binding.spLocation)

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
    }

    private fun setAnimation() {
        val topToBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.top_to_bottom)
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.tvCreateAccount.startAnimation(scaleAnimation)
        binding.etBio.startAnimation(topToBottomAnimation)
        binding.llEmail.startAnimation(topToBottomAnimation)
        binding.llCounty.startAnimation(topToBottomAnimation)
        binding.llBirthday.startAnimation(topToBottomAnimation)
        binding.llHeight.startAnimation(topToBottomAnimation)
        binding.llWeight.startAnimation(topToBottomAnimation)
        binding.btnSave.startAnimation(topToBottomAnimation)
    }

    private fun setUpHandlers() {
        binding.btnUploadBackground.setOnClickListener {
//            val metrics = requireContext().resources.displayMetrics
//            val dpWidth = metrics.widthPixels / metrics.density - 150
//            val dpHeight = metrics.heightPixels / metrics.density - 150

            ImagePicker.with(this)
                .crop(5f,2.24f)
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
        binding.btnSave.setOnClickListener {
            if(checkInput(binding.etUsername.text.toString(),
                          binding.btnBirthday.text.toString(),
                          binding.btnHeight.text.toString(),
                          binding.btnWeight.text.toString())){

                saveUser(binding.etUsername.text.toString(),
                         binding.etBio.text.toString(),
                         binding.spLocation.selectedItem.toString(),
                         binding.btnBirthday.text.toString(),
                         binding.btnHeight.text.toString(),
                         binding.btnWeight.text.toString())

                writeUIDToSharedPref()

                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
                activity?.finish()

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
//                val drawable = Drawable.createFromPath(filePath.absolutePath)

                binding.ivBackground.setImageURI(fileUri)

                val fileName = fileUri.lastPathSegment
                val permission = fileName?.substring(fileName.indexOf('.'))
                val backgroundRef = storageRef.child("images/${uid}/${"background$permission"}")

                val uploadTask = backgroundRef.putFile(fileUri)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.upload_successful), Toast.LENGTH_SHORT).show()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
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
//                val drawable = Drawable.createFromPath(filePath.absolutePath)
                binding.ivProfile.setImageURI(fileUri)

                val fileName = fileUri.lastPathSegment
                val permission = fileName?.substring(fileName.indexOf('.'))
                val profileRef = storageRef.child("images/${uid}/${"profile$permission"}")

                val uploadTask = profileRef.putFile(fileUri)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.upload_successful), Toast.LENGTH_SHORT).show()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),  requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        }
}