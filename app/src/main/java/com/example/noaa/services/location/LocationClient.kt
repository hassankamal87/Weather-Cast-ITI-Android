package com.example.noaa.services.location

import android.annotation.SuppressLint
import android.util.Log
import com.example.noaa.homeactivity.view.TAG
import com.example.noaa.model.Coordinate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class LocationClient private constructor(
    private val fusedLocationClient: FusedLocationProviderClient
): LocationClientInterface {

    private val sharedFlow: MutableSharedFlow<Coordinate> = MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
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
       // requestNewLocationData() //--> some times do not update when i called it
        getCurrentLocationTwo()
        return sharedFlow
    }

    /*@SuppressLint("MissingPermission")
    fun requestNewLocationData() {
        Log.d(TAG, "requestNewLocationData: ")
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = Long.MAX_VALUE
        locationRequest.fastestInterval = Long.MAX_VALUE
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

            Log.d(TAG, "onLocationResult: location come from callback")


            val result = sharedFlow.tryEmit(Coordinate(latitude, longitude))
            Log.w(TAG, "onLocationResult: $result")
        }
    }*/


    @SuppressLint("MissingPermission")
    fun getCurrentLocationTwo(){
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener {
            sharedFlow.tryEmit(Coordinate(it.latitude, it.longitude))
        }
    }

}

