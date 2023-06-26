package com.example.noaa.services.network

import com.example.noaa.model.WeatherResponse

sealed class ApiState{
    class Success(val weatherResponse: WeatherResponse): ApiState()
    class Failure(val errMsg: String): ApiState()
    object Loading: ApiState()
}
