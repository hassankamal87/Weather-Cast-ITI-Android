package com.example.noaa.splash.view
//the National Oceanic and Atmospheric Administration
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.postOnAnimationDelayed
import com.example.noaa.databinding.ActivityMainBinding
import com.example.noaa.homeactivity.view.HomeActivity
import com.example.services.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

const val TAG = "hassankamal"
const val My_LOCATION_PERMISSION_ID = 5005
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var fusedClient: FusedLocationProviderClient
    lateinit var locationClient: LocationClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieAnimation.postOnAnimationDelayed(3000){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient = LocationClient.getInstance(fusedClient, locationCallBack)

        Log.d(TAG, "onCreate: ${locationClient.hashCode()}")
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            if (isLocationIsEnabled()) {
                locationClient.getCurrentLocation()
            }else{
                showLocationDialog()
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationIsEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )
    }

    private fun checkPermission(): Boolean {
        var result = false
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        }
        return result
    }

    private fun showLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Location services are disabled. Do you want to enable them?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                val latitude = 30.0131
                val longitude = 31.2089
                Log.d(TAG, "Giza lat -> $latitude ### long -> $longitude")
            }
        val dialog = builder.create()
        dialog.show()
    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation

            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude

            Log.d(TAG, "lat -> $latitude ### long -> $longitude")

        }
    }
}