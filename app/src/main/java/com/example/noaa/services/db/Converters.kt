package com.example.noaa.services.db

import androidx.room.TypeConverter
import com.example.noaa.model.Alert
import com.example.noaa.model.Current
import com.example.noaa.model.Daily
import com.example.noaa.model.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromCurrentToString(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun fromStringToCurrent(stringCurrent: String): Current {
        return Gson().fromJson(stringCurrent, Current::class.java)
    }

    @TypeConverter
    fun fromDailyToString(daily: List<Daily>): String {
        return Gson().toJson(daily)
    }

    @TypeConverter
    fun fromStringToDaily(stringDaily: String): List<Daily> {
        return Gson().fromJson(stringDaily, object : TypeToken<List<Daily>>() {}.type)
    }


    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>): String {
        return Gson().toJson(hourly)
    }

    @TypeConverter
    fun fromStringToHourly(stringHourly: String): List<Hourly> {
        return Gson().fromJson(stringHourly, object : TypeToken<List<Hourly>>() {}.type)
    }


    @TypeConverter
    fun fromAlertToString(alert: List<Alert>?): String? {
        alert?.let {
            return Gson().toJson(alert)
        }
        return null
    }

    @TypeConverter
    fun fromStringToAlert(stringAlert: String?): List<Alert>? {
        stringAlert?.let {
            return Gson().fromJson(stringAlert, object : TypeToken<List<Alert>>() {}.type)
        }
        return null
    }

}