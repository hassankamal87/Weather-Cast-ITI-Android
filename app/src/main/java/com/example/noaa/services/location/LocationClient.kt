package com.example.noaa.services.location

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.Coordinate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LocationClient private constructor(
    private val fusedLocationClient: FusedLocationProviderClient
): LocationClientInterface {

    private val sharedFlow: MutableSharedFlow<Coordinate> = MutableSharedFlow()
    companion object {
        private var instance: LocationClient? = null

        fun getInstance(
            fusedLocationClient: FusedLocationProviderClient
        ): LocationClient {
            return instance ?: synchronized(this) {
                instance ?: LocationClient(fusedLocationClient).also {
                    instance = it
                }
            }
        }
    }

    override fun getCurrentLocation():Flow<Coordinate> {
        Log.d(TAG, "getCurrentLocation: ")
        requestNewLocationData()
        return sharedFlow
    }

    @SuppressLint("MissingPermission")
    override fun requestNewLocationData() {
        Log.d(TAG, "requestNewLocationData: ")
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )
    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation
            Log.d(TAG, "onLocationResult: $lastLocation")
            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude
            Log.d(TAG, "lat -> $latitude ### long -> $longitude")

            sharedFlow.tryEmit(Coordinate(latitude, longitude))
        }
    }


}

