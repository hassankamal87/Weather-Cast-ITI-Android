package com.example.noaa.services.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noaa.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceToFav(place: Place)

    @Delete
    suspend fun deletePlaceFromFav(place: Place)

    @Query("SELECT * FROM place")
    fun getAllFavouritePlaces(): Flow<List<Place>>
}