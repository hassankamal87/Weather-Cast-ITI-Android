package com.example.noaa.model

import android.content.Context
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.db.LocalSource
import com.example.noaa.services.location.LocationClientInterface
import com.example.noaa.services.network.ConcreteRemoteSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: ConcreteRemoteSource,
    private val locationClient: LocationClientInterface,
    private val localSource: LocalSource
) : RepoInterface {

    companion object {
        private var instance: Repo? = null

        fun getInstance(
            remoteSource: ConcreteRemoteSource,
            locationClient: LocationClientInterface,
            localSource: LocalSource
        ): Repo {
            return instance ?: synchronized(this) {
                instance ?: Repo(remoteSource, locationClient, localSource).also { instance = it }
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

    override suspend fun insertPlaceToFav(context: Context, place: Place) {
        localSource.insertPlaceToFav(context, place)
    }

    override suspend fun deletePlaceFromFav(context: Context, place: Place) {
        localSource.deletePlaceFromFav(context, place)
    }

    override fun getAllFavouritePlaces(context: Context): Flow<List<Place>> {
        return localSource.getAllFavouritePlaces(context)
    }

    override suspend fun insertCashedData(context: Context, weatherResponse: WeatherResponse) {
        localSource.insertCashedData(context, weatherResponse)
    }

    override suspend fun deleteCashedData(context: Context) {
        localSource.deleteCashedData(context)
    }

    override fun getCashedData(context: Context): Flow<WeatherResponse> {
        return localSource.getCashedData(context)
    }


}