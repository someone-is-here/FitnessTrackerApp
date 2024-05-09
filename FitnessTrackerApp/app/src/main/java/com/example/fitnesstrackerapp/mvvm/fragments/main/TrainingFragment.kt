package com.example.fitnesstrackerapp.mvvm.fragments.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.adapters.TrainingAdapter
import com.example.fitnesstrackerapp.databinding.FragmentTrainingBinding
import com.example.fitnesstrackerapp.mvvm.viewmodels.MainViewModel
import com.example.fitnesstrackerapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.fitnesstrackerapp.other.SortType
import com.example.fitnesstrackerapp.other.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class TrainingFragment: Fragment(), EasyPermissions.PermissionCallbacks {
    private val viewModel: MainViewModel by viewModels()
    private var binding: FragmentTrainingBinding? = null
    private lateinit var trainingAdapter: TrainingAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTrainingBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        requestPermissions()
        setUpRecycleView()

        when(viewModel.sortType) {
            SortType.DATE -> binding!!.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding!!.spFilter.setSelection(1)
            SortType.DISTANCE -> binding!!.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding!!.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding!!.spFilter.setSelection(4)
        }

        binding!!.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> {
                        viewModel.sortRuns(SortType.DATE)
                        trainingAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        viewModel.sortRuns(SortType.RUNNING_TIME)
                        trainingAdapter.notifyDataSetChanged()
                    }
                    2 -> {
                        viewModel.sortRuns(SortType.DISTANCE)
                        trainingAdapter.notifyDataSetChanged()
                    }
                    3 -> {
                        viewModel.sortRuns(SortType.AVG_SPEED)
                        trainingAdapter.notifyDataSetChanged()
                    }
                    4 -> {
                        viewModel.sortRuns(SortType.CALORIES_BURNED)
                        trainingAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.trainings.observe(viewLifecycleOwner, Observer {
            trainingAdapter.submitList(it)
        })

        binding!!.fab.setOnClickListener {
            findNavController().navigate(R.id.action_trainingFragment_to_trackingFragment)
        }

    }

    private fun setUpRecycleView() = binding!!.rvRuns.apply {
        trainingAdapter = TrainingAdapter()
        adapter = trainingAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermissions(){
        // Already have permissions
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }

        // Request permissions
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                requireContext().getString(R.string.accepting_permissions_request),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                requireContext().getString(R.string.accepting_permissions_request),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) { }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else {
          requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}