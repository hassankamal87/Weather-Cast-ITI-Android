package com.example.noaa.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noaa.model.AlarmItem
import com.example.noaa.model.RepoInterface
import com.example.noaa.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(private val repo: RepoInterface) : ViewModel() {

    private val _alarmsMutableStateFlow: MutableStateFlow<List<AlarmItem>> = MutableStateFlow(
        emptyList()
    )
    val alarmsStateFlow: StateFlow<List<AlarmItem>> get() = _alarmsMutableStateFlow


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

    fun getCashedData(): Flow<WeatherResponse>{
        return repo.getCashedData()
    }


}