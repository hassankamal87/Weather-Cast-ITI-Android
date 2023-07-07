package com.example.noaa.services.db

import android.content.Context
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource private constructor(context: Context): LocalSource {

    private val favouriteDao: FavouriteDao by lazy {
        FavouriteDatabase.getInstance(context).getDao()
    }

    companion object{
        private var instance: ConcreteLocalSource? = null

        fun getInstance(context: Context): ConcreteLocalSource{
            return instance?: ConcreteLocalSource(context).also {
                instance = it
            }
        }
    }

    override suspend fun insertPlaceToFav(place: Place) {
        favouriteDao.insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(place: Place) {
        favouriteDao.deletePlaceFromFav(place)
    }

    override fun getAllFavouritePlaces(): Flow<List<Place>> {
        return favouriteDao.getAllFavouritePlaces()
    }

    override suspend fun insertCashedData(weatherResponse: WeatherResponse) {
        favouriteDao.insertCashedData(weatherResponse)
    }

    override suspend fun deleteCashedData() {
        favouriteDao.deleteCashedData()
    }

    override fun getCashedData(): Flow<WeatherResponse> {
        return favouriteDao.getCashedData()
    }

    override suspend fun insertAlarm(alarmItem: AlarmItem) {
        favouriteDao.insertAlarm(alarmItem)
    }

    override suspend fun deleteAlarm(alarmItem: AlarmItem) {
        favouriteDao.deleteAlarm(alarmItem)
    }

    override fun getAllAlarms(): Flow<List<AlarmItem>> {
        return favouriteDao.getAllAlarms()
    }
}