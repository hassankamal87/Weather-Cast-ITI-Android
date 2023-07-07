package com.example.noaa.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noaa.databinding.ItemAlertBinding
import com.example.noaa.model.AlarmItem
import com.example.noaa.utilities.Functions

class AlertRecyclerAdapter :
    ListAdapter<AlarmItem, AlertRecyclerAdapter.AlertViewHolder>(RecyclerDiffUtilAlarmItem()) {

    lateinit var binding: ItemAlertBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.apply {
            binding.apply {
                tvKind.text = currentItem.kind

                tvFromDate.text =
                    Functions.formatLongToAnyString(currentItem.time, "dd MMM yyyy")

                tvFromTime.text =
                    Functions.formatLongToAnyString(currentItem.time, "hh:mm a")

            }
        }
    }

    inner class AlertViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class RecyclerDiffUtilAlarmItem : DiffUtil.ItemCallback<AlarmItem>() {
    override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
        return oldItem == newItem
    }
}