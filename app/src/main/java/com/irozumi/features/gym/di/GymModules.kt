package com.irozumi.features.gym.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irozumi.features.gym.data.repository.GymRepositoryImpl
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class GymViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Usamos .isAssignableFrom con la clase Java correcta para evitar el error de candidatos alternativos
        if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
            val repository = GymRepositoryImpl()
            return GymViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}