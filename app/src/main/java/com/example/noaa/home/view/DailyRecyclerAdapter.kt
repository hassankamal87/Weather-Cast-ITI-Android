package com.example.noaa.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.R
import com.example.noaa.databinding.ItemDaysBinding
import com.example.noaa.model.Daily
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyRecyclerAdapter(private val sharedPreferences: SharedPreferences) :
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

                if (sharedPreferences.getString(Constants.LANGUAGE, null) == Constants.ARABIC) {
                    tvDayDays.text =
                        Functions.formatDayOfWeek(currentItem.dt, tvDayDays.context, "ar")
                } else {
                    tvDayDays.text =
                        Functions.formatDayOfWeek(currentItem.dt, tvDayDays.context, "en")
                }
                tvStatusDays.text = currentItem.weather[0].description
                when (sharedPreferences.getString(Constants.TEMPERATURE, "null")) {
                    Constants.KELVIN -> tvDegreeDays.text = String.format(
                        "%.0f/%.0f°${tvDegreeDays.context.getString(R.string.k)}",
                        currentItem.temp.max + 273.15, currentItem.temp.min + 273.15
                    )

                    Constants.FAHRENHEIT -> tvDegreeDays.text = String.format(
                        "%.0f/%.0f°${tvDegreeDays.context.getString(R.string.f)}",
                        currentItem.temp.max * 9 / 5 + 32, currentItem.temp.min * 9 / 5 + 32
                    )

                    else -> tvDegreeDays.text =
                        String.format(
                            "%.0f/%.0f°${tvDegreeDays.context.getString(R.string.c)}",
                            currentItem.temp.max,
                            currentItem.temp.min
                        )
                }
                Functions.setIcon(currentItem.weather[0].icon, ivIconDays)
            }

        }
    }

    inner class DailyViewHolder(private val binding: ItemDaysBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class RecyclerDiffUtilDaily : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}