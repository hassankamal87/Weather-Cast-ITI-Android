package com.example.noaa.homeactivity.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.noaa.R
import com.example.noaa.databinding.ActivityHomeBinding
import com.example.noaa.databinding.InitialDialogLayoutBinding
import com.example.noaa.homeactivity.viewmodel.SharedViewModel
import com.example.noaa.homeactivity.viewmodel.SharedViewModelFactory
import com.example.noaa.model.Coordinate
import com.example.noaa.model.Repo
import com.example.noaa.utilities.Constants
import com.example.noaa.services.location.LocationClient
import com.example.noaa.services.network.RemoteSource
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


public const val TAG = "hassankamal"
const val My_LOCATION_PERMISSION_ID = 5005

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    private lateinit var sharedViewModelFactory: SharedViewModelFactory
    private lateinit var sharedViewModel: SharedViewModel

    lateinit var savedCoordinateLocale: Coordinate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)


        sharedViewModelFactory = SharedViewModelFactory(
            Repo.getInstance(
                RemoteSource,
                LocationClient.getInstance(LocationServices.getFusedLocationProviderClient(this))
            ),
            getSharedPreferences(Constants.SETTING, MODE_PRIVATE)
        )
        sharedViewModel =
            ViewModelProvider(this, sharedViewModelFactory)[SharedViewModel::class.java]


        lifecycleScope.launch {
            sharedViewModel.locationStatusStateFlow.collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        Constants.SHOW_DIALOG -> showLocationDialog()
                        Constants.REQUEST_PERMISSION -> requestPermissions()
                        Constants.SHOW_INITIAL_DIALOG -> showInitialSettingDialog()
                        Constants.TRANSITION_TO_MAP -> transitionToMap()
                    }
                }
            }
        }

        lifecycleScope.launch {
            sharedViewModel.coordinateStateFlow.collect {
                Log.w(TAG, "onViewCreated: from fragment ${it.latitude}")
                Log.w(TAG, "onViewCreated: from fragment ${it.longitude}")
                if (it.latitude != 0.0) {
                    sharedViewModel.getWeatherData(Coordinate(it.latitude, it.longitude), "en")
                }
            }
        }


        sharedViewModel.getLocationDataLocally()
        lifecycleScope.launch {
            sharedViewModel.savedLocationStateFlow.collect {
                savedCoordinateLocale = it
            }
        }

    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        sharedViewModel.getLocation(this)
    }

    private fun transitionToMap() {
        Log.d(TAG, "onResume: transition to map")
        if (savedCoordinateLocale.latitude == 0.0) {
            navController.navigate(R.id.mapFragment)
        } else {
            sharedViewModel.getWeatherData(savedCoordinateLocale, "en")
        }
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


    private fun showLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Location services are disabled. Do you want to enable them?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                transitionToMap()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showInitialSettingDialog() {
        val bindingInitialLayoutDialog = InitialDialogLayoutBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(bindingInitialLayoutDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingInitialLayoutDialog.btnOkInitial.setOnClickListener {
            val checkedBtn =
                bindingInitialLayoutDialog.radioGroupSettingLocationInitial.checkedRadioButtonId

            if (checkedBtn == bindingInitialLayoutDialog.radioSettingGpsInitial.id) {
                sharedViewModel.setLocationChoice(Constants.GPS)
            } else {
                sharedViewModel.setLocationChoice(Constants.MAP)
            }
            sharedViewModel.getLocation(this)

            dialog.dismiss()
        }
        dialog.show()
    }
}
