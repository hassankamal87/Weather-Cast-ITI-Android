package com.example.noaa.homeactivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.services.location.LocationClient

class HomeActivityViewModelFactory(val locationClient: LocationClient): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(HomeActivityViewModel::class.java)){
            return HomeActivityViewModel(locationClient) as T
        }
        return super.create(modelClass, extras)
    }
}