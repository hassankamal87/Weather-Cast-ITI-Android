package com.example.noaa.homeactivity.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.noaa.R
import com.example.noaa.databinding.ActivityHomeBinding
import com.example.noaa.home.view.HomeFragment
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModel
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModelFactory
import com.example.noaa.utilities.Constants
import com.example.services.location.LocationClient
import com.google.android.gms.location.LocationServices


public const val TAG = "hassankamal"
const val My_LOCATION_PERMISSION_ID = 5005

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    lateinit var homeActivityViewModelFactory: HomeActivityViewModelFactory
    lateinit var activityViewModel: HomeActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)


        homeActivityViewModelFactory = HomeActivityViewModelFactory(
            LocationClient.getInstance(
                LocationServices.getFusedLocationProviderClient(this)
            )
        )
        activityViewModel =
            ViewModelProvider(this, homeActivityViewModelFactory)[HomeActivityViewModel::class.java]

        activityViewModel.locationStatusLiveData.observe(this){
            when(it){
                Constants.SHOW_DIALOG -> showLocationDialog()
                Constants.REQUEST_PERMISSION -> requestPermissions()
            }
        }

        activityViewModel.coordinateLiveData.observe(this){
            Log.d(TAG, "latitude -> ${it.latitude}")
            Log.d(TAG, "longitude -> ${it.longitude}")
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        activityViewModel.getLocation(this)
    }


    private fun requestPermissions() {
        Log.d(TAG, "requestPermissions: ")
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )
    }


    private fun showLocationDialog() {
        Log.d(TAG, "showLocationDialog: ")
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
}
