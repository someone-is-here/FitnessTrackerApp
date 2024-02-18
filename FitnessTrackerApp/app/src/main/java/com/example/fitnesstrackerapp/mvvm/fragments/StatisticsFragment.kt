package com.example.fitnesstrackerapp.mvvm.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.FragmentStatisticsBinding
import com.example.fitnesstrackerapp.mvvm.viewmodels.StatisticsViewModel
import com.example.fitnesstrackerapp.other.CustomMarkerView
import com.example.fitnesstrackerapp.other.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: StatisticsViewModel by viewModels()
    private var binding: FragmentStatisticsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpBarChart()
    }
    private fun setUpBarChart(){
        val barChart = binding!!.barChart
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
        }
        barChart.apply {
            description.text = "Average Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding!!.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer{
            it?.let{
                val km = it / 100f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "$totalDistance km"
                binding!!.tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAverageSpeed.observe(viewLifecycleOwner, Observer{
            it?.let{
               val averageSpeed = round(it * 10f) / 10f
                val avgSpeedString = "$averageSpeed km/h"
                binding!!.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer{
            it?.let{
               val totalCalories = "${it}kcal"
                binding!!.tvTotalCalories.text = totalCalories
            }
        })
        viewModel.trainingSortedByDate.observe(viewLifecycleOwner, Observer{
            it?.let {
                val barChart = binding!!.barChart
                val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAvgSpeed, "Average Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.yellow)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerView(it.reversed(),
                                                    requireContext(), R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }
}