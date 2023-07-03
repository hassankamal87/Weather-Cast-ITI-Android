package com.example.noaa.model

import android.content.Context
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

    suspend fun insertPlaceToFav(place: Place)

    suspend fun deletePlaceFromFav(place: Place)

    fun getAllFavouritePlaces(): Flow<List<Place>>

    suspend fun insertCashedData(weatherResponse: WeatherResponse)

    suspend fun deleteCashedData()

    fun getCashedData(): Flow<WeatherResponse>

    fun writeStringToSettingSP(key: String, value: String)

    fun readStringFromSettingSP(key: String): String

    fun writeFloatToSettingSP(key: String, value: Float)

    fun readFloatFromSettingSP(key: String): Float
}