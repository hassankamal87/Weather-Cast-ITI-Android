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
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationManagerCompat
import com.example.noaa.R
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Coordinate
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.network.ConcreteRemoteSource
import com.example.noaa.services.network.RemoteSource
import com.example.noaa.services.notification.NotificationChannelHelper
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val item = intent?.getSerializableExtra(Constants.ALARM_ITEM) as AlarmItem
        var messageFromApi = "The weather has cleared up and conditions are now good"
        Log.w(TAG, "onReceive: ${item.latitude},   ${item.longitude}", )
        runBlocking {
            context?.let {
                ConcreteLocalSource.getInstance(it).deleteAlarm(item)

                /*RemoteSource.getWeatherResponse(Coordinate(item.latitude, item.longitude), "en")
                    .collectLatest { weatherData->
                        if(weatherData.isSuccessful){
                            val mes = weatherData.body()!!.alerts?.get(0)?.description ?: "null"
                            if(mes!="null")
                            messageFromApi = mes
                        }
                    }*/

                val mes = SettingSharedPref.getInstance(context)
                    .readStringFromSettingSP(Constants.ALERT_DESCRIPTION)
                if (mes != "null") {
                    messageFromApi = mes
                }
            }
        }


        when (item.kind) {
            Constants.NOTIFICATION -> {
                context?.let {
                    createNotification(it, messageFromApi)
                }
            }

            Constants.ALERT -> {
                context?.let {
                    createAlertDialog(it, messageFromApi)
                }
            }
        }



    }

    @SuppressLint("MissingPermission")
    fun createNotification(context: Context, messageFromApi: String) {

        val builder = NotificationChannelHelper.createNotification(
            context,
            messageFromApi
        )
        with(NotificationManagerCompat.from(context)) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }

    }


    private fun createAlertDialog(context: Context, messageFromApi: String) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_alarm, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.alert_description)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.alert_stop)

        dialogMessage.text = messageFromApi
        val mediaPlayer = MediaPlayer.create(context, R.raw.alert)


        val builder = AlertDialog.Builder(context, R.style.MyCustomAlertDialogStyle)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.setCancelable(false)
        val window = dialog.window
        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.TOP)
        dialog.show()

        dialog.setOnShowListener {
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            mediaPlayer.stop()
        }
    }

}