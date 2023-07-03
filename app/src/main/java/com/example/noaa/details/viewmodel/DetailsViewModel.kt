package com.example.noaa.details.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noaa.model.Coordinate
import com.example.noaa.model.RepoInterface
import com.example.noaa.services.network.ApiState
import com.example.noaa.services.sharepreferences.SettingSharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repo: RepoInterface
): ViewModel() {
    private val _weatherResponseMutableStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherResponseStateFlow: StateFlow<ApiState> get() = _weatherResponseMutableStateFlow

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

    fun readStringFromSettingSP(key: String): String{
        return repo.readStringFromSettingSP(key)
    }
}