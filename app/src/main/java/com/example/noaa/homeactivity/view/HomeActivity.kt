package com.example.noaa.homeactivity.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.SearchManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.noaa.R
import com.example.noaa.databinding.ActivityHomeBinding
import com.example.noaa.databinding.InitialDialogLayoutBinding
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModel
import com.example.noaa.homeactivity.viewmodel.HomeActivityViewModelFactory
import com.example.noaa.model.Repo
import com.example.noaa.utilities.Constants
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.google.android.gms.location.LocationServices


public const val TAG = "hassankamal"
const val My_LOCATION_PERMISSION_ID = 5005

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var bindingInitialLayoutDialog: InitialDialogLayoutBinding
    private var checkedBtn: Int = 0
    private lateinit var navController: NavController

    private lateinit var homeActivityViewModelFactory: HomeActivityViewModelFactory
    lateinit var activityViewModel: HomeActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        bindingInitialLayoutDialog = InitialDialogLayoutBinding.inflate(layoutInflater)

        homeActivityViewModelFactory = HomeActivityViewModelFactory(
            Repo.getInstance(
                RemoteSource, LocationClient.getInstance(
                    LocationServices.getFusedLocationProviderClient(this)
                )
            ), getSharedPreferences(Constants.SETTING, MODE_PRIVATE)
        )
        activityViewModel =
            ViewModelProvider(this, homeActivityViewModelFactory)[HomeActivityViewModel::class.java]


        activityViewModel.locationStatusLiveData.observe(this) {
            when (it) {
                Constants.SHOW_DIALOG -> showLocationDialog()
                Constants.REQUEST_PERMISSION -> requestPermissions()
                Constants.SHOW_INITIAL_DIALOG -> showInitialSettingDialog()
                Constants.TRANSITION_TO_MAP -> transitionToMap()
            }
        }

        activityViewModel.coordinateLiveData.observe(this) {
            Log.d(TAG, "latitude -> ${it.latitude}")
            Log.d(TAG, "longitude -> ${it.longitude}")
            Toast.makeText(this, "${it.latitude} \n${it.longitude} ", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        activityViewModel.getLocation(this)
    }

    private fun transitionToMap() {
        Log.d(TAG, "onResume: transition to map")

//        val latitude = 26.8206
//        val longitude = 30.8025
//        val zoomLevel = 15
//        val geoUri = "geo:$latitude,$longitude?z=$zoomLevel"
//        val intent = Intent(Intent.ACTION_PICK, Uri.parse(geoUri))
//        startActivityForResult(intent,Constants.REQUEST_CODE_MAP)
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

    private fun showInitialSettingDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        dialog.setContentView(bindingInitialLayoutDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingInitialLayoutDialog.btnOkInitial.setOnClickListener {
            checkedBtn =
                bindingInitialLayoutDialog.radioGroupSettingLocationInitial.checkedRadioButtonId

            if (checkedBtn == bindingInitialLayoutDialog.radioSettingGpsInitial.id) {
                activityViewModel.setLocationChoice(Constants.GPS)
            } else {
                activityViewModel.setLocationChoice(Constants.MAP)
            }
            activityViewModel.getLocation(this)

            dialog.dismiss()
        }
        dialog.show()
    }
}
