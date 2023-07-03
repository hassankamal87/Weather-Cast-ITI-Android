package com.example.noaa.services.db

import android.content.Context
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource private constructor(): LocalSource {

    companion object{
        private var instance: ConcreteLocalSource? = null

        fun getInstance(): ConcreteLocalSource{
            return instance?: ConcreteLocalSource().also {
                instance = it
            }
        }
    }

    override suspend fun insertPlaceToFav(context: Context, place: Place) {
        FavouriteDatabase.getInstance(context).getDao().insertPlaceToFav(place)
    }

    override suspend fun deletePlaceFromFav(context: Context, place: Place) {
        FavouriteDatabase.getInstance(context).getDao().deletePlaceFromFav(place)
    }

    override fun getAllFavouritePlaces(context: Context): Flow<List<Place>> {
        return FavouriteDatabase.getInstance(context).getDao().getAllFavouritePlaces()
    }

    override suspend fun insertCashedData(context: Context, weatherResponse: WeatherResponse) {
        FavouriteDatabase.getInstance(context).getDao().insertCashedData(weatherResponse)
    }

    override suspend fun deleteCashedData(context: Context) {
        FavouriteDatabase.getInstance(context).getDao().deleteCashedData()
    }

    override fun getCashedData(context: Context): Flow<WeatherResponse> {
        return FavouriteDatabase.getInstance(context).getDao().getCashedData()
    }
}