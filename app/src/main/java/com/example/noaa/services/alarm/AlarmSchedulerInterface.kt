package com.example.noaa.services.alarm

import android.content.Context
import com.example.noaa.model.AlarmItem

interface AlarmSchedulerInterface {
    fun createAlarm(item: AlarmItem, context: Context)
    fun cancelAlarm(item: AlarmItem, context: Context)
}