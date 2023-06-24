package com.example.noaa.homeactivity.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.noaa.services.location.LocationClient

class HomeActivityViewModelFactory(
    private val locationClient: LocationClient,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(HomeActivityViewModel::class.java)) {
            return HomeActivityViewModel(locationClient, sharedPreferences) as T
        }
        return super.create(modelClass, extras)
    }
}