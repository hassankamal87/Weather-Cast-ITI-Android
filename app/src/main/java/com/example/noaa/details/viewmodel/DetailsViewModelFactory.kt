package com.example.noaa.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.noaa.model.RepoInterface

class DetailsViewModelFactory(
    private val repo: RepoInterface
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repo) as T
        }
        return super.create(modelClass, extras)
    }
}