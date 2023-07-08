package com.example.noaa.alert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.Coordinate
import com.example.noaa.model.RepoInterface
import com.example.noaa.services.alarm.AlarmScheduler
import com.example.noaa.services.sharepreferences.SettingSharedPref
import com.example.noaa.utilities.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(private val repo: RepoInterface, private val alarmScheduler: AlarmScheduler) : ViewModel() {

    private val _alarmsMutableStateFlow: MutableStateFlow<List<AlarmItem>> = MutableStateFlow(
        emptyList()
    )
    val alarmsStateFlow: StateFlow<List<AlarmItem>> get() = _alarmsMutableStateFlow

    private val _coordinateMutableStateFlow: MutableStateFlow<Coordinate> = MutableStateFlow(
        Coordinate(0.0,0.0)
    )
    val coordinateStateFlow: StateFlow<Coordinate> get() = _coordinateMutableStateFlow


    fun insertAlarm(alarmItem: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlarm(alarmItem)
        }
    }

    fun deleteAlarm(alarmItem: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlarm(alarmItem)
        }
    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repo.getAllAlarms().collectLatest {
                _alarmsMutableStateFlow.value = it
            }
        }
    }

    fun getCashedData() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCashedData().collectLatest {
                _coordinateMutableStateFlow.value = Coordinate(it.lat,  it.lon)
            }
        }
    }

    fun createAlarmScheduler(alarmItem: AlarmItem, context: Context){
        alarmScheduler.createAlarm(alarmItem, context)
    }

    fun cancelAlarmScheduler(alarmItem: AlarmItem, context: Context){
        alarmScheduler.cancelAlarm(alarmItem, context)
    }

    fun readStringFromSettingSP(key: String): String{
        return repo.readStringFromSettingSP(key)
    }

    fun isNotificationEnabled() = readStringFromSettingSP(Constants.NOTIFICATION) == Constants.ENABLE

}