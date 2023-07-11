package com.data.source

import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Place
import com.example.noaa.model.RepoInterface
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRepo(
    private val places: MutableList<Place> = mutableListOf(),
    private var weather: WeatherResponse,
    private var stringReadValue: String
): RepoInterface {

    override suspend fun deletePlaceFromFav(place: Place) {
        places.remove(place)
    }

    override suspend fun insertPlaceToFav(place: Place) {
        places.add(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> {
        return flowOf(places)
    }

    override suspend fun getWeatherResponse(
        coordinate: Coordinate,
        language: String
    ): Flow<Response<WeatherResponse>> {
        return flowOf(Response.success(weather))
    }

    override fun readStringFromSettingSP(key: String): String {
        return stringReadValue
    }

    override fun writeStringToSettingSP(key: String, value: String) {
        stringReadValue = value
    }

    override fun getCurrentLocation(): Flow<Coordinate> {
        TODO("Not yet implemented")
    }



    override suspend fun insertCashedData(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCashedData() {
        TODO("Not yet implemented")
    }

    override fun getCashedData(): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }


    override suspend fun insertAlarm(alarmItem: AlarmItem) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarmItem: AlarmItem) {
        TODO("Not yet implemented")
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        TODO("Not yet implemented")
    }

    override fun writeFloatToSettingSP(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun readFloatFromSettingSP(key: String): Float {
        TODO("Not yet implemented")
    }
}