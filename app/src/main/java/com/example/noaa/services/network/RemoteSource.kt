package com.example.noaa.services.network

import com.example.noaa.model.Coordinate
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

object RemoteSource: ConcreteRemoteSource {
    override suspend  fun getWeatherResponse(
        coordinate: Coordinate,
        language: String
    ): Flow<Response<WeatherResponse>> {
        val response = ApiClient.apiService.getWeatherResponse(coordinate.latitude, coordinate.longitude, language)
        return flowOf(response)
    }

}