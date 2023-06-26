package com.example.noaa.services.network

import com.example.noaa.model.WeatherResponse
import com.example.noaa.utilities.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("onecall?units=metric&appid=${Constants.WEATHER_API_KEY}")
    suspend fun getWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String = "en"
    ): Response<WeatherResponse>

}