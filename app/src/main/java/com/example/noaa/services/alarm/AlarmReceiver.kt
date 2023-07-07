package com.example.noaa.services.alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.AlarmItem
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.notification.NotificationChannelHelper
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val item = intent?.getSerializableExtra(Constants.ALARM_ITEM) as AlarmItem
        Log.w(TAG, "onReceive: ${item.time.toInt()}")
        when (item.kind) {
            Constants.NOTIFICATION -> {
                context?.let {
                    createNotification(it)
                }
            }

            Constants.ALERT -> {}
        }

        runBlocking {
            context?.let {
                ConcreteLocalSource.getInstance(it).deleteAlarm(item)
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun createNotification(context: Context) {
        var messageFromApi = "The weather has cleared up and conditions are now good"
        runBlocking {
            val mes = SettingSharedPref.getInstance(context)
                .readStringFromSettingSP(Constants.ALERT_DESCRIPTION)
            if (mes != "null") {
                messageFromApi = mes
            }
        }
        val builder = NotificationChannelHelper.createNotification(
            context,
            messageFromApi
        )
        with(NotificationManagerCompat.from(context)) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }

    }


}