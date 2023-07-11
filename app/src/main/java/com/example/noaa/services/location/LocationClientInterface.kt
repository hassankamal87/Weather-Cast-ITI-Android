package com.example.noaa.services.location

import com.example.noaa.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface LocationClientInterface {
    fun getCurrentLocation(): Flow<Coordinate>
}