package com.data.source

import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import com.example.noaa.services.db.LocalSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    private val places: MutableList<Place> = mutableListOf(),
    private val alarms: MutableList<AlarmItem> = mutableListOf(),
    private var weathers: MutableList<WeatherResponse> = mutableListOf()
) : LocalSource {

    override suspend fun insertPlaceToFav(place: Place) {
        places.add(place)
    }

    override suspend fun deletePlaceFromFav(place: Place) {
        places.remove(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> {
        return flowOf(places)
    }

    override suspend fun insertCashedData(weatherResponse: WeatherResponse) {
        weathers.add(weatherResponse)
    }

    override suspend fun deleteCashedData() {
        weathers.clear()
    }

    override fun getCashedData(): Flow<WeatherResponse>? {
        if(weathers.isNullOrEmpty()){
            return null
        }
        return flowOf(weathers[0])
    }

    override suspend fun insertAlarm(alarmItem: AlarmItem) {
        alarms.add(alarmItem)
    }

    override suspend fun deleteAlarm(alarmItem: AlarmItem) {
        alarms.remove(alarmItem)
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        return flowOf(alarms)
    }
}