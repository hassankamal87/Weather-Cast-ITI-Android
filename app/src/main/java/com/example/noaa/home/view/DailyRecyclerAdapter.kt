package com.example.noaa.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.R
import com.example.noaa.databinding.ItemDaysBinding
import com.example.noaa.model.Daily
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions

class DailyRecyclerAdapter :
    ListAdapter<Daily, DailyRecyclerAdapter.DailyViewHolder>(RecyclerDiffUtilDaily()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemDaysBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class DailyViewHolder(private val binding: ItemDaysBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun onBind(currentItem: Daily){
                binding.apply {

                    if (SettingSharedPref.getInstance(tvDayDays.context)
                            .readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC
                    ) {
                        tvDayDays.text =
                            Functions.formatDayOfWeek(currentItem.dt, tvDayDays.context, "ar")
                    } else {
                        tvDayDays.text =
                            Functions.formatDayOfWeek(currentItem.dt, tvDayDays.context, "en")
                    }
                    tvStatusDays.text = currentItem.weather[0].description
                    when (SettingSharedPref.getInstance(tvDayDays.context)
                        .readStringFromSettingSP(Constants.TEMPERATURE)) {
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

}

class RecyclerDiffUtilDaily : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}