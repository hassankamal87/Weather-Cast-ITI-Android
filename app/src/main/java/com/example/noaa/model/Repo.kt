package com.example.noaa.model

import com.example.noaa.services.location.LocationClientInterface
import com.example.noaa.services.network.ConcreteRemoteSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: ConcreteRemoteSource,
    private val locationClient: LocationClientInterface
) : RepoInterface {

    companion object {
        private var instance: Repo? = null

        fun getInstance(
            remoteSource: ConcreteRemoteSource,
            locationClient: LocationClientInterface
        ): Repo {
            return instance ?: synchronized(this) {
                instance ?: Repo(remoteSource, locationClient).also { instance = it }
            }
        }

    }

    override suspend fun getWeatherResponse(
        coordinate: Coordinate,
        language: String
    ): Flow<Response<WeatherResponse>> {
        return remoteSource.getWeatherResponse(coordinate, language)
    }

    override fun getCurrentLocation(): Flow<Coordinate> {
        return locationClient.getCurrentLocation()
    }


}