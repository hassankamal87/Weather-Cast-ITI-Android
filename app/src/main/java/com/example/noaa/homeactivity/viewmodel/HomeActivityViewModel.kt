package com.example.noaa.homeactivity.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.telephony.CarrierConfigManager.Gps
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noaa.model.Coordinate
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.LocationUtility
import com.example.noaa.services.location.LocationClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivityViewModel(
    private val locationClient: LocationClient,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _locationStatusMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val locationStatusLiveData: LiveData<String>
        get() = _locationStatusMutableLiveData

    private val _coordinateMutableLiveData: MutableLiveData<Coordinate> = MutableLiveData()
    val coordinateLiveData: LiveData<Coordinate>
        get() = _coordinateMutableLiveData


    fun setLocationChoice(choice: String) {
        sharedPreferences.edit().putString(Constants.LOCATION, choice).apply()
    }

    fun getLocation(context: Context) {
        when (sharedPreferences.getString(Constants.LOCATION, "null")) {
            Constants.GPS -> {
                if (LocationUtility.checkPermission(context)) {
                    if (LocationUtility.isLocationIsEnabled(context)) {
                        viewModelScope.launch {
                            locationClient.getCurrentLocation().collectLatest {
                                _coordinateMutableLiveData.postValue(it)
                            }
                        }
                    } else {
                        _locationStatusMutableLiveData.postValue(Constants.SHOW_DIALOG)
                    }
                } else {
                    _locationStatusMutableLiveData.postValue(Constants.REQUEST_PERMISSION)
                }
            }

            Constants.MAP -> {
                _locationStatusMutableLiveData.postValue(Constants.TRANSITION_TO_MAP)
            }

            else -> {
                _locationStatusMutableLiveData.postValue(Constants.SHOW_INITIAL_DIALOG)
            }
        }
    }


}