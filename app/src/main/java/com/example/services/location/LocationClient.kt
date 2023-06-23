package com.example.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager

import android.os.Looper
import android.provider.Settings

import android.util.Log

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

const val TAG = "hassankamal"
private const val My_LOCATION_PERMISSION_ID = 5005

class LocationClient(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context, private val locationCallback: LocationCallback
) {

    var latitude: Double? = null
    var longitude: Double? = null

    fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationIsEnabled()) {
                requestNewLocationData()
            } else {
                Log.d(TAG, "location is NOT enabled")
                showLocationDialog()
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermission(): Boolean {
        Log.d(TAG, "checkingPermission....")
        var result = false
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        }
        Log.d(TAG, "checkPermission Result -> $result")
        return result
    }

    private fun isLocationIsEnabled(): Boolean {
        Log.d(TAG, "checking enabled..")
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.d(TAG, "requestNewLocationData....")
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun requestPermissions() {
        Log.d(TAG, "requesting Permission....")
        if (context is Activity) {
            val activity = context as Activity
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                My_LOCATION_PERMISSION_ID
            )
        }

    }


    private fun showLocationDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Location services are disabled. Do you want to enable them?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                latitude = 30.0444
                longitude = 31.2357
                Log.d(TAG, "Cairo lat -> $latitude ### long -> $longitude")
            }
        val dialog = builder.create()
        dialog.show()
    }
}


/*
private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation

            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude

            Log.d(TAG, "lat -> $latitude ### long -> $longitude")

        }
    }
 */