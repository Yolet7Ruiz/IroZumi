package com.irozumi.features.gym.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel

@Suppress("UNCHECKED_CAST")
class GymViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
            return GymViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}