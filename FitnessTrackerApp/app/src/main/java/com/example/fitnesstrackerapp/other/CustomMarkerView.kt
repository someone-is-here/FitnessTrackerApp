package com.example.fitnesstrackerapp.other

import android.app.Activity
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.TextView
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.db.Training
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class CustomMarkerView (
    val trainings: List<Training>,
    context: Context,
    layoutId: Int
): MarkerView(context, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width/1.2f, -height.toFloat()/1.2f)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        if(e == null){
            return
        }

        val curRunId = e.x.toInt()
        val training = trainings[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = training.timestamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDate).text = dateFormat.format(calendar.time)

        val avgSpeed = "${training.avgSpeedInKMH} km/h"
        findViewById<TextView>(R.id.tvAvgSpeed).text = avgSpeed

        val distanceInKm = "${training.distanceInMeters / 1000f} km"
        findViewById<TextView>(R.id.tvDistance).text = distanceInKm

        findViewById<TextView>(R.id.tvDuration).text = TrackingUtility.getFormattedStopWatchTime(training.timeInMills)

        val caloriesBurned = "${training.caloriesBurned} kcal"
        findViewById<TextView>(R.id.tvCaloriesBurned).text = caloriesBurned
    }
}