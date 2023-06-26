package com.example.noaa.model

import com.google.gson.annotations.SerializedName


data class WeatherResponse(

    @SerializedName("current")val currentDay: Current,
    @SerializedName("daily")val days: List<Daily>,
    @SerializedName("hourly")val hours: List<Hourly>,
    @SerializedName("lat")val latitude: Double,
    @SerializedName("lon")val longitude: Double,
    @SerializedName("timezone")val zoneName: String,
    @SerializedName("timezone_offset")val timezone_offset: Int
)