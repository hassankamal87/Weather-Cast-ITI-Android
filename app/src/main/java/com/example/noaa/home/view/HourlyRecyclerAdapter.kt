package com.example.noaa.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.ItemHoursBinding
import com.example.noaa.model.Hourly
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HourlyRecyclerAdapter :
    ListAdapter<Hourly, HourlyRecyclerAdapter.HourlyViewHolder>(RecyclerDiffUtil()) {

    lateinit var binding: ItemHoursBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHoursBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.apply {
            binding.tvDateHours.text = formatDateStamp(currentItem.dt)
            binding.tvTimeHours.text = formatTimestamp(currentItem.dt)
            binding.tvDegreeHours.text = String.format("%.0f°C", currentItem.temp)
        }

    }

    inner class HourlyViewHolder(val binding: ItemHoursBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun formatTimestamp(timestamp: Int): String {
        val sdf = SimpleDateFormat("h a", Locale.ENGLISH)
        val date = Date(timestamp.toLong() * 1000)
        return sdf.format(date)
    }

    private fun formatDateStamp(timestamp: Int): String {
        val sdf = SimpleDateFormat("d MMM", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp.toLong() * 1000
        val targetDay = calendar.get(Calendar.DAY_OF_YEAR)
        return when (targetDay) {
            currentDay -> "Today"
            currentDay + 1 -> "Tomorrow"
            else -> sdf.format(calendar.time)
        }
    }
}

class RecyclerDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}
