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
import com.example.noaa.databinding.ItemHoursBinding
import com.example.noaa.model.Hourly
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.Functions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HourlyRecyclerAdapter() :
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
            binding.apply {
                if (SettingSharedPref.getInstance(tvDateHours.context).readStringFromSettingSP(Constants.LANGUAGE) == Constants.ARABIC) {
                    tvDateHours.text =
                        Functions.formatDateStamp(currentItem.dt, tvDateHours.context, "ar")
                    tvTimeHours.text = Functions.formatTimestamp(currentItem.dt, "ar")
                }else{
                    tvDateHours.text =
                        Functions.formatDateStamp(currentItem.dt, tvDateHours.context, "en")
                    tvTimeHours.text = Functions.formatTimestamp(currentItem.dt, "en")
                }

                tvDegreeHours.text = String.format("%.0f°C", currentItem.temp)
                when (SettingSharedPref.getInstance(tvDegreeHours.context).readStringFromSettingSP(Constants.TEMPERATURE)) {
                    Constants.KELVIN -> tvDegreeHours.text = String.format(
                        "%.0f°${tvDegreeHours.context.getString(R.string.k)}",
                        currentItem.temp + 273.15
                    )

                    Constants.FAHRENHEIT -> tvDegreeHours.text = String.format(
                        "%.0f°${tvDegreeHours.context.getString(R.string.f)}",
                        currentItem.temp * 9 / 5 + 32
                    )

                    else -> tvDegreeHours.text =
                        String.format(
                            "%.0f°${tvDegreeHours.context.getString(R.string.c)}",
                            currentItem.temp
                        )
                }
                Functions.setIcon(currentItem.weather[0].icon, ivStatusIconHours)
            }
        }

    }

    inner class HourlyViewHolder(val binding: ItemHoursBinding) :
        RecyclerView.ViewHolder(binding.root)




}

class RecyclerDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }

}
