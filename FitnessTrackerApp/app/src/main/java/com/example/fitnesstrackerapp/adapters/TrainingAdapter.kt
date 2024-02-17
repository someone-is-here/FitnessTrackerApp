package com.example.fitnesstrackerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.db.Training
import com.example.fitnesstrackerapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TrainingAdapter: RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {
    inner class TrainingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    private val differCallback = object: DiffUtil.ItemCallback<Training>(){
        override fun areItemsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)
    fun submitList(list: List<Training>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        return TrainingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(training.img).into(holder.itemView.findViewById<ImageView>(R.id.ivRunImage))

            val calendar = Calendar.getInstance().apply {
                timeInMillis = training.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            holder.itemView.findViewById<TextView>(R.id.tvDate).text = dateFormat.format(calendar.time)

            val avgSpeed = "${training.avgSpeedInKMH} km/h"
            holder.itemView.findViewById<TextView>(R.id.tvAvgSpeed).text = avgSpeed

            val distanceInKm = "${training.distanceInMeters / 1000f} km"
            holder.itemView.findViewById<TextView>(R.id.tvDistance).text = distanceInKm

            holder.itemView.findViewById<TextView>(R.id.tvTime).text = TrackingUtility.getFormattedStopWatchTime(training.timeInMills)

            val caloriesBurned = "${training.caloriesBurned} kcal"
            holder.itemView.findViewById<TextView>(R.id.tvCalories).text = caloriesBurned
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}