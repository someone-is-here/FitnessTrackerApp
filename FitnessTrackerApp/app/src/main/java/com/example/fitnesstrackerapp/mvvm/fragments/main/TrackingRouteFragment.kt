package com.example.fitnesstrackerapp.mvvm.fragments.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentTrackingRouteBinding
import com.example.fitnesstrackerapp.db.Training
import com.example.fitnesstrackerapp.mvvm.fragments.additional.CancelTrackingDialog
import com.example.fitnesstrackerapp.mvvm.viewmodels.MainViewModel
import com.example.fitnesstrackerapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstrackerapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnesstrackerapp.other.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstrackerapp.other.Constants.CIRCLE_RADIUS
import com.example.fitnesstrackerapp.other.Constants.MAP_ZOOM
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_COLOR
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_WIDTH
import com.example.fitnesstrackerapp.other.Constants.STROKE_COLOR
import com.example.fitnesstrackerapp.other.Constants.STROKE_WIDTH
import com.example.fitnesstrackerapp.other.TrackingUtility
import com.example.fitnesstrackerapp.services.Polyline
import com.example.fitnesstrackerapp.services.TrackingService
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Calendar
import kotlin.math.round
import kotlin.math.roundToLong

const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"

@AndroidEntryPoint
class TrackingRouteFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingRouteBinding
    private var map: GoogleMap?= null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var circlePosition: Circle? = null

    private var currentTimeInMillis: Long = 0L

    //    @set:Inject
    var weight = 80f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTrackingRouteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }

        binding.mapView.onCreate(savedInstanceState)

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        binding.btnContinue.setOnClickListener {
            binding.btnContinue.visibility = View.GONE
            binding.btnToggleRun.visibility = View.VISIBLE
            toggleRun()
        }

        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        binding.btnCancelRun.setOnClickListener {
            sendCommandToService(ACTION_PAUSE_SERVICE)
            showCancelTrackingDialog()
        }

        binding.btnFinishRun.setOnClickListener {
            circlePosition?.remove()
            circlePosition = null
            zoomToSeeWholeRoute()
            endRunAndSaveToDB()
        }

        setInitialValues()

        subscribeToObservers()

        initMapCords()

    }

    private fun setInitialValues() {
        binding.btnToggleRun.text = requireContext().getString(R.string.start)
        binding.btnToggleRun.visibility = View.VISIBLE
        binding.btnFinishRun.visibility = View.GONE
        binding.btnCancelRun.visibility = View.GONE
        binding.btnContinue.visibility = View.GONE
        binding.tvTimer.text = "00:00:00"
        binding.tvAvgSpeed.text = "0,00"
        binding.tvCaloriesBurned.text = "0"
        binding.tvDistance.text = "0,000"
        currentTimeInMillis = 0
    }

    @SuppressLint("MissingPermission")
    private fun initMapCords() {
        val priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        MAP_ZOOM
                    )
                )
                val circleOptions = CircleOptions()
                    .fillColor(POLYLINE_COLOR)
                    .center(LatLng(location.latitude, location.longitude))
                    .radius(CIRCLE_RADIUS.toDouble())
                    .strokeColor(STROKE_COLOR)
                    .strokeWidth(STROKE_WIDTH)

                circlePosition = map?.addCircle(circleOptions)
            }
            .addOnFailureListener { exception ->
                Timber.d("Location initial failed with exception: $exception")
            }
    }


    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            updateMapAccordingToCoordinate()
        })
        TrackingService.timeRunInSeconds.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it * 1000L
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
            updateValues()
        })
    }
    private fun toggleRun(){
        if(isTracking){
            binding.btnCancelRun.visibility = View.VISIBLE
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis > 0L){
            binding.btnToggleRun.visibility = View.GONE
            binding.btnContinue.text = requireContext().getString(R.string.continue_str)
            binding.btnFinishRun.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.VISIBLE
        } else if(isTracking){
            binding.btnToggleRun.text = requireContext().getString(R.string.stop)
            binding.btnCancelRun.visibility = View.VISIBLE
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun updateMapAccordingToCoordinate(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 0) {
            val circleOptions = CircleOptions()
                .fillColor(POLYLINE_COLOR)
                .center(pathPoints.last().last())
                .radius(CIRCLE_RADIUS.toDouble())
                .strokeWidth(STROKE_WIDTH)
                .strokeColor(STROKE_COLOR)

            if (circlePosition != null) {
                circlePosition?.remove()
                circlePosition = map?.addCircle(circleOptions)
            }
        }
    }

    private fun zoomToSeeWholeRoute(){
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(cord in polyline){
                bounds.include(cord)
            }
        }

        circlePosition?.remove()

        val mapView = binding.mapView
        try{
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )}catch (_:Exception){
            return
        }
    }

    private fun updateValues(){
        var distanceInMeters = 0.0f

        for(polyline in pathPoints){
            distanceInMeters += TrackingUtility.calculatePolylineLength(polyline)
        }

        binding.tvDistance.text = String.format("%.3f", distanceInMeters / 1000f)

        val avgSpeed = (round((distanceInMeters / 1000f) /
                (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f)

        if(!avgSpeed.isNaN()){
            binding.tvAvgSpeed.text = avgSpeed.toString()
        }

        binding.tvCaloriesBurned.text = ((distanceInMeters / 1000f) * weight).toInt().toString()
    }

    private fun endRunAndSaveToDB(){
        map?.snapshot { bmp ->
            var distanceInMeters = 0

            for(polyline in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) /
                    (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val training = Training(bmp, dateTimeStamp,
                avgSpeed, distanceInMeters,
                currentTimeInMillis, caloriesBurned)

            viewModel.insertTraining(training)

            com.google.android.material.snackbar.Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                requireContext().getString(R.string.training_completed),
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
            ).show()
            stopRun()
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 0) {
            val circleOptions = CircleOptions()
                .fillColor(POLYLINE_COLOR)
                .center(pathPoints.last().last())
                .radius(CIRCLE_RADIUS.toDouble())
                .strokeWidth(STROKE_WIDTH)
                .strokeColor(STROKE_COLOR)

            if (circlePosition != null) {
                circlePosition?.remove()
                circlePosition = map?.addCircle(circleOptions)
            }
        }

        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

        binding.btnContinue.visibility = View.GONE
        binding.btnFinishRun.visibility = View.GONE
    }
    override fun onStart() {
        binding.mapView.invalidate()
        binding.mapView.onStart()
        addAllPolylines()

        super.onStart()

    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun showCancelTrackingDialog(){
        val dialog = CancelTrackingDialog()

        dialog.apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)

    }
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        setInitialValues()
        findNavController().navigate(R.id.action_trackingRouteFragment_to_trainingFragment)
    }
}