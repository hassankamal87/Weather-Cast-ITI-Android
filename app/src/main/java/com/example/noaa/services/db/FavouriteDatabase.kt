package com.example.noaa.services.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noaa.model.Place

@Database(entities = [Place::class], version = 1)
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