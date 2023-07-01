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

    suspend fun insertPlaceToFav(context: Context, place: Place)

    suspend fun deletePlaceFromFav(context: Context, place: Place)

    fun getAllFavouritePlaces(context: Context): Flow<List<Place>>
}