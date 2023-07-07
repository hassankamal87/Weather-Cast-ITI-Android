package com.example.noaa.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity
data class WeatherResponse(
    @PrimaryKey
    val id: Int = 0,
    @SerializedName("current")val current: Current,
    @SerializedName("daily")val daily: List<Daily>,
    @SerializedName("hourly")val hourly: List<Hourly>,
    @SerializedName("lat")val lat: Double,
    @SerializedName("lon")val lon: Double,
    @SerializedName("timezone")val timezone: String,
    @SerializedName("timezone_offset")val timezone_offset: Int,
    @SerializedName("alerts")val alerts: List<Alert>?
)