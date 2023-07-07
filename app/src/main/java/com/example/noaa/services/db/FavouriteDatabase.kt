package com.example.noaa.services.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Place
import com.example.noaa.model.WeatherResponse

@Database(entities = [Place::class, WeatherResponse::class, AlarmItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class FavouriteDatabase: RoomDatabase() {

    abstract fun getDao(): FavouriteDao

    companion object {
        private var instance: FavouriteDatabase? = null

        fun getInstance(context: Context): FavouriteDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavouriteDatabase::class.java,
                    "fav_db"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}