package com.example.noaa.model

import android.content.Context
import com.example.noaa.services.db.ConcreteLocalSource
import com.example.noaa.services.db.LocalSource
import com.example.noaa.services.location.LocationClientInterface
import com.example.noaa.services.network.ConcreteRemoteSource
import com.example.noaa.services.sharepreferences.SettingSPInterface
import com.example.noaa.services.sharepreferences.SettingSharedPref
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: ConcreteRemoteSource,
    private val locationClient: LocationClientInterface,
    private val localSource: LocalSource,
    private val sharedSetting: SettingSPInterface
) : RepoInterface {

    companion object {
        private var instance: Repo? = null

        fun getInstance(
            remoteSource: ConcreteRemoteSource,
            locationClient: LocationClientInterface,
            localSource: LocalSource,
            sharedSetting: SettingSPInterface
        ): Repo {
            return instance ?: synchronized(this) {
                instance ?: Repo(remoteSource, locationClient, localSource, sharedSetting).also { instance = it }
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

    override suspend fun insertPlaceToFav(place: Place) {
        localSource.insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(place: Place) {
        localSource.deletePlaceFromFav(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> {
        return localSource.getAllFavouritePlaces()
    }

    override suspend fun insertCashedData(weatherResponse: WeatherResponse) {
        localSource.insertCashedData(weatherResponse)
    }

    override suspend fun deleteCashedData() {
        localSource.deleteCashedData()
    }

    override fun getCashedData(): Flow<WeatherResponse> {
        return localSource.getCashedData()
    }

    override fun writeStringToSettingSP(key: String, value: String) {
        sharedSetting.writeStringToSettingSP(key, value)
    }

    override fun readStringFromSettingSP(key: String): String {
        return sharedSetting.readStringFromSettingSP(key)
    }

    override fun writeFloatToSettingSP(key: String, value: Float) {
        sharedSetting.writeFloatToSettingSP(key, value)
    }

    override fun readFloatFromSettingSP(key: String): Float {
        return sharedSetting.readFloatFromSettingSP(key)
    }


}