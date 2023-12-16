package com.example.fitnesstrackerapp.mvvm.fragments

import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentSetUpBinding
import com.example.fitnesstrackerapp.databinding.FragmentTrackingBinding
import com.example.fitnesstrackerapp.mvvm.viewmodels.MainViewModel
import com.example.fitnesstrackerapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstrackerapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnesstrackerapp.other.Constants.CIRCLE_RADIUS
import com.example.fitnesstrackerapp.other.Constants.MAP_ZOOM
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_COLOR
import com.example.fitnesstrackerapp.other.Constants.POLYLINE_WIDTH
import com.example.fitnesstrackerapp.other.TrackingUtility
import com.example.fitnesstrackerapp.services.Polyline
import com.example.fitnesstrackerapp.services.Polylines
import com.example.fitnesstrackerapp.services.TrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var binding: FragmentTrackingBinding? = null
    private var map: GoogleMap?= null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var circlePosition: Circle? = null

    private var currentTimeInMillis: Long = 0L
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTrackingBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.mapView.onCreate(savedInstanceState)
        binding!!.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        binding!!.btnToggleRun.setOnClickListener {
           toggleRun()
        }

        subscribeToObservers()
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
          binding!!.tvTimer.text = formattedTime
        })
    }
    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding!!.btnToggleRun.text = "Start"
            binding!!.btnFinishRun.visibility = View.VISIBLE
        } else {
            binding!!.btnToggleRun.text = "Stop"
            binding!!.btnFinishRun.visibility = View.GONE
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
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size == 1) {
            val circleOptions = CircleOptions()
                .fillColor(POLYLINE_COLOR)
                .center(pathPoints.last().last())
                .radius(CIRCLE_RADIUS.toDouble())

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
        binding!!.mapView.onResume()
    }
    override fun onStart() {
        super.onStart()
        binding!!.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding!!.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding!!.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding!!.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding!!.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.mapView.onDestroy()
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
}