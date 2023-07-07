package com.example.noaa.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.noaa.model.AlarmItem
import com.example.noaa.utilities.Constants

class AlarmScheduler(context: Context) : AlarmSchedulerInterface {
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(AlarmManager::class.java)
    }

    companion object {
        private var instance: AlarmScheduler? = null

        fun getInstance(context: Context): AlarmScheduler =
            instance ?: synchronized(this) {
                instance ?: AlarmScheduler(context).also { instance = it }
            }
    }

    override fun createAlarm(item: AlarmItem, context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(Constants.ALARM_ITEM, item)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            // item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            item.time,
            PendingIntent.getBroadcast(
                context,
                item.time.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelAlarm(item: AlarmItem, context: Context) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.time.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}