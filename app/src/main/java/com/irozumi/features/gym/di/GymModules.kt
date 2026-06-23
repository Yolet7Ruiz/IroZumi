package com.irozumi.features.gym.di

import com.irozumi.features.gym.data.repository.GymRepositoryImpl
import com.irozumi.features.gym.domain.repository.GymRepository
import androidx.lifecycle.ViewModel
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel
import java.lang.IllegalArgumentException

// Si usas inyección manual express debido al tiempo límite de 5 días,
// puedes instanciar tu ViewModel directamente usando una Factory básica en tu Screen o MainActivity:
@Suppress("UNCHECKED_CAST")
class GymViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
            val repository = GymRepositoryImpl()
            return GymViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}