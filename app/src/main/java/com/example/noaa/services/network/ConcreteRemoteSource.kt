package com.example.noaa.services.network

import com.example.noaa.model.Coordinate
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface ConcreteRemoteSource {
    suspend fun getWeatherResponse(coordinate: Coordinate, language: String): Flow<Response<WeatherResponse>>
}