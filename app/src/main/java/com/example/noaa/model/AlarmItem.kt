package com.example.noaa.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm_item")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true)
    val time: Long,
    val kind: String,
    val latitude: Double,
    val longitude: Double
): Serializable
