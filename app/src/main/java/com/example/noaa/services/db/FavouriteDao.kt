package com.example.noaa.services.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceToFav(place: Place)

    @Delete
    suspend fun deletePlaceFromFav(place: Place)

    @Query("SELECT * FROM place")
    fun getAllFavouritePlaces(): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashedData(weatherResponse: WeatherResponse)

    @Query("DELETE FROM WeatherResponse")
    suspend fun deleteCashedData()

    @Query("SELECT * FROM WeatherResponse")
    fun getCashedData(): Flow<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmItem: AlarmItem)

    @Delete
    suspend fun deleteAlarm(alarmItem: AlarmItem)

    @Query("SELECT * FROM alarm_item")
    fun getAllAlarms(): Flow<List<AlarmItem>>

}