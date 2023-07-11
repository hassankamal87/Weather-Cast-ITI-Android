package com.data.source

import com.example.noaa.model.Coordinate
import com.example.noaa.services.location.LocationClientInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocationClient(private val coordinate: Coordinate): LocationClientInterface {
    override fun getCurrentLocation(): Flow<Coordinate> {
        return flowOf(coordinate)
    }
}