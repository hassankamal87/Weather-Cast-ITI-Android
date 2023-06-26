package com.example.noaa.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.ItemDaysBinding
import com.example.noaa.model.Daily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyRecyclerAdapter :
    ListAdapter<Daily, DailyRecyclerAdapter.DailyViewHolder>(RecyclerDiffUtilDaily()) {

    lateinit var binding: ItemDaysBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDaysBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.apply {
            binding.apply {

                tvDayDays.text = formatDayOfWeek(currentItem.dt)
                tvStatusDays.text = currentItem.weather[0].description
                tvDegreeDays.text = String.format("%.0f/%.0f°C",currentItem.temp.max,currentItem.temp.min)

            }

        }
    }

    inner class DailyViewHolder(private val binding: ItemDaysBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun formatDayOfWeek(timestamp: Int): String {
        val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
        val calendar: Calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp.toLong() * 1000
        val targetDay = calendar.get(Calendar.DAY_OF_YEAR)
        return when (targetDay) {
            currentDay -> "Today"
            currentDay + 1 -> "Tomorrow"
            else -> sdf.format(calendar.time).uppercase(Locale.ROOT)
        }
    }
}

class RecyclerDiffUtilDaily : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}