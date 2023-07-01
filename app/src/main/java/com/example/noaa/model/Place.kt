package com.example.noaa.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "place")
data class Place(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var cityName: String,
    var latitude: Double,
    var longitude: Double
): Serializable
