package com.example.noaa.services.location

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.Coordinate
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationClientInterface {
    fun getCurrentLocation(): Flow<Coordinate>
    fun requestNewLocationData()
}