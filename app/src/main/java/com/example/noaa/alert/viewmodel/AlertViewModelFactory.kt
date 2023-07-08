package com.example.noaa.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.noaa.model.RepoInterface
import com.example.noaa.services.alarm.AlarmScheduler

class AlertViewModelFactory(private val repo: RepoInterface, private val alarmScheduler: AlarmScheduler) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(repo, alarmScheduler) as T
        }
        return super.create(modelClass, extras)
    }
}