package com.data.source

import com.example.noaa.model.Coordinate
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.network.ConcreteRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRemoteSource(
    private var weather: WeatherResponse
): ConcreteRemoteSource {
    override suspend fun getWeatherResponse(
        coordinate: Coordinate,
        language: String
    ): Flow<Response<WeatherResponse>> {
        return flowOf(Response.success(weather))
    }
}