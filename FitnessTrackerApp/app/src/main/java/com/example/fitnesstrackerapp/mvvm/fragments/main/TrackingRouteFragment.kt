package com.example.fitnesstrackerapp.mvvm.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentTrackingRouteBinding
import com.example.fitnesstrackerapp.db.Training
import com.example.fitnesstrackerapp.mvvm.viewmodels.MainViewModel
import com.example.fitnesstrackerapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstrackerapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnesstrackerapp.other.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstrackerapp.other.Constants.CIRCLE_RADIUS
import com.example.fitnesstrackerapp.other.Constants.MAP_ZOOM
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_COLOR
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_WIDTH
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
            showCancelTrackingDialog()
        }

        binding.btnFinishRun.setOnClickListener {
            circlePosition?.remove()
            circlePosition = null
            zoomToSeeWholeRoute()
            endRunAndSaveToDB()
        }

        binding.btnFinishRun.visibility = View.GONE
        binding.btnCancelRun.visibility = View.GONE
        binding.btnContinue.visibility = View.GONE

        subscribeToObservers()

        initMapCords()
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
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it

            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
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
        if(!isTracking){
            binding.btnToggleRun.visibility = View.GONE
            binding.btnContinue.text = requireContext().getString(R.string.continue_str)
            binding.btnFinishRun.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.VISIBLE
        } else {
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

            circlePosition?.remove()
            circlePosition = map?.addCircle(circleOptions)
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

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
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
        if(pathPoints.isNotEmpty() && pathPoints.last().size == 1) {
            val circleOptions = CircleOptions()
                .fillColor(POLYLINE_COLOR)
                .center(pathPoints.last().last())
                .radius(CIRCLE_RADIUS.toDouble())

            circlePosition?.remove()
            circlePosition = map?.addCircle(circleOptions)
        }
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            val circleOptions = CircleOptions()
                .fillColor(POLYLINE_COLOR)
                .center(lastLatLng)
                .radius(CIRCLE_RADIUS.toDouble())

            map?.addPolyline(polylineOptions)

            circlePosition?.remove()
            circlePosition = map?.addCircle(circleOptions)

        }
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()

        binding.btnContinue.visibility = View.GONE
        binding.btnFinishRun.visibility = View.GONE
    }
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
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
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(requireContext().getString(R.string.cancel_the_run))
            .setMessage(requireContext().getString(R.string.sure_cancel_run))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(requireContext().getString(R.string.yes)){ _, _ ->
                stopRun()
            }
            .setNegativeButton(requireContext().getString(R.string.no)) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.show()
    }
    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingRouteFragment_to_trainingFragment)
    }
}