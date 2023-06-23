package com.example.services.location

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest


class LocationClient private constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback
) {

    companion object {
        private var instance: LocationClient? = null

        fun getInstance(
            fusedLocationClient: FusedLocationProviderClient,
            locationCallback: LocationCallback
        ): LocationClient {
            return instance ?: synchronized(this) {
                instance ?: LocationClient(fusedLocationClient, locationCallback).also {
                    instance = it
                }
            }
        }
    }

    fun getCurrentLocation() {
        requestNewLocationData()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


}

