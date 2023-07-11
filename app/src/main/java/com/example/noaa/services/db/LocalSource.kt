package com.example.noaa.services.db

import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertPlaceToFav(place: Place)

    suspend fun deletePlaceFromFav(place: Place)

    fun getAllFavouritePlaces(): Flow<List<Place>>

    suspend fun insertCashedData(weatherResponse: WeatherResponse)

    suspend fun deleteCashedData()

    fun getCashedData(): Flow<WeatherResponse>?

    suspend fun insertAlarm(alarmItem: AlarmItem)

    suspend fun deleteAlarm(alarmItem: AlarmItem)

    fun getAllAlarms(): Flow<List<AlarmItem>>
}