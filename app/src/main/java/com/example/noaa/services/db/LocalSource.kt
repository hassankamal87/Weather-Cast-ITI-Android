package com.example.noaa.services.db

import android.content.Context
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertPlaceToFav(context: Context, place: Place)

    suspend fun deletePlaceFromFav(context: Context, place: Place)

    fun getAllFavouritePlaces(context: Context): Flow<List<Place>>

    suspend fun insertCashedData(context: Context, weatherResponse: WeatherResponse)

    suspend fun deleteCashedData(context: Context)

    fun getCashedData(context: Context): Flow<WeatherResponse>
}