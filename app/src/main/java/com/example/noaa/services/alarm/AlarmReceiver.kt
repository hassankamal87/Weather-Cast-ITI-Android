package com.example.noaa.services.alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.example.noaa.R
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Coordinate
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.network.ApiClient
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.notification.NotificationChannelHelper
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.PermissionUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val item = intent?.getSerializableExtra(Constants.ALARM_ITEM) as AlarmItem

        //this message is shown when api response have no alerts
        var messageFromApi = "The weather has cleared up and conditions are now good"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                context?.let {

                    ConcreteLocalSource.getInstance(it).deleteAlarm(item)

                    val mes = getAlertMessageFromApi(it, item)

                    if (mes != "null") {
                        messageFromApi = mes
                    }

                    if (isNotificationEnabled(it)) {
                        Log.w(TAG, "onReceive: here2" )
                        withContext(Dispatchers.Main) {
                            when (item.kind) {
                                Constants.NOTIFICATION -> createNotification(
                                    it,
                                    messageFromApi,
                                    item.zoneName
                                )

                                Constants.ALERT -> createAlertDialog(
                                    it,
                                    messageFromApi,
                                    item.zoneName
                                )
                            }
                        }
                    }
                }
            } catch (_: Exception) {
            } finally {
                cancel()
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun createNotification(context: Context, messageFromApi: String, zoneName: String) {
        val builder = NotificationChannelHelper.createNotification(
            context, messageFromApi, zoneName
        )
        with(NotificationManagerCompat.from(context)) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }
        val mediaPlayer = MediaPlayer.create(context, R.raw.pop_up)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
    }


    private fun createAlertDialog(context: Context, messageFromApi: String, zoneName: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_alarm, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.alert_description)
        val zoneNameTV = dialogView.findViewById<TextView>(R.id.zoneName)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.alert_stop)


        dialogMessage.text = messageFromApi
        zoneNameTV.text = zoneName

        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)

        val builder = AlertDialog.Builder(context, R.style.MyCustomAlertDialogStyle)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setOnShowListener {
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        val window = dialog.window
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.TOP)

        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(15000)
                withContext(Dispatchers.Main) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                        createNotification(context, messageFromApi, zoneName)
                    }
                }
            } catch (_: Exception) {
            } finally {
                cancel()
            }
        }

        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            mediaPlayer.stop()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
        }
    }

    private fun isNotificationEnabled(context: Context) = SettingSharedPref.getInstance(context)
        .readStringFromSettingSP(Constants.NOTIFICATION) != Constants.DISABLE

    private suspend fun getAlertMessageFromApi(
        context: Context,
        alarmItem: AlarmItem
    ): String {
        var mes = "null"
        if (PermissionUtility.checkConnection(context)) {
            RemoteSource.getWeatherResponse(
                Coordinate(alarmItem.latitude, alarmItem.longitude), "en"
            ).collectLatest { weatherData ->
                if (weatherData.isSuccessful) {
                    mes = weatherData.body()!!.alerts?.get(0)?.description ?: "null"
                }
            }
        }
        return mes
    }
}