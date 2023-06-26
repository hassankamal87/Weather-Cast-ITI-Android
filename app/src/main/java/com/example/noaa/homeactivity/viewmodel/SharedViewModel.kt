package com.example.noaa.homeactivity.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noaa.model.Coordinate
import com.example.noaa.model.RepoInterface
import com.example.noaa.services.network.ApiState
import com.example.noaa.utilities.Constants
import com.example.noaa.utilities.LocationUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SharedViewModel(
    private val repo: RepoInterface,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _locationStatusMutableLiveData: MutableLiveData<String> = MutableLiveData()
    val locationStatusLiveData: LiveData<String>
        get() = _locationStatusMutableLiveData

    private val _coordinateMutableLiveData: MutableLiveData<Coordinate> = MutableLiveData()
    val coordinateLiveData: LiveData<Coordinate>
        get() = _coordinateMutableLiveData

    private val _weatherResponseMutableStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherResponseStateFlow: StateFlow<ApiState>
        get() = _weatherResponseMutableStateFlow

    private val _savedLocationMutableStateFlow: MutableStateFlow<Coordinate> = MutableStateFlow(Coordinate(0.0,0.0))
    val savedLocationStateFlow: StateFlow<Coordinate>
        get() = _savedLocationMutableStateFlow

    fun setLocationChoice(choice: String) {
        sharedPreferences.edit().putString(Constants.LOCATION, choice).apply()
    }

    fun setLocationData(coordinate: Coordinate){
        sharedPreferences.edit().putFloat(Constants.LATITUDE, coordinate.latitude.toFloat()).apply()
        sharedPreferences.edit().putFloat(Constants.LONGITUDE, coordinate.longitude.toFloat()).apply()
    }

    fun getLocationData(){
        val lat = sharedPreferences.getFloat(Constants.LATITUDE, 0.0f)
        val long = sharedPreferences.getFloat(Constants.LONGITUDE, 0.0f)
        _savedLocationMutableStateFlow.value = Coordinate(lat.toDouble(), long.toDouble())
    }


    fun getLocation(context: Context) {

        when (sharedPreferences.getString(Constants.LOCATION, "null")) {
            Constants.GPS -> {
                if (LocationUtility.checkPermission(context)) {
                    if (LocationUtility.isLocationIsEnabled(context)) {
                        viewModelScope.launch {
                            repo.getCurrentLocation().collectLatest {
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

    fun getWeatherData(coordinate: Coordinate, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherResponse(coordinate, language).catch {
                _weatherResponseMutableStateFlow.value = ApiState.Failure(it.message!!)
            }.collect { weatherResponse ->
                if (weatherResponse.isSuccessful) {
                    _weatherResponseMutableStateFlow.value =
                        ApiState.Success(weatherResponse.body()!!)
                } else {
                    _weatherResponseMutableStateFlow.value =
                        ApiState.Failure(weatherResponse.message())
                }
            }
        }
    }


}