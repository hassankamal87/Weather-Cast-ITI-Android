package com.example.noaa.services.db

import android.content.Context
import com.example.noaa.model.Place
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertPlaceToFav(context: Context, place: Place)

    suspend fun deletePlaceFromFav(context: Context, place: Place)

    fun getAllFavouritePlaces(context: Context): Flow<List<Place>>
}