package com.example.noaa.model

import android.util.Log
import com.example.noaa.homeactivity.view.TAG
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {

    suspend fun getWeatherResponse(
        coordinate: Coordinate,
        language: String
    ): Flow<Response<WeatherResponse>>

    fun getCurrentLocation():Flow<Coordinate>
}