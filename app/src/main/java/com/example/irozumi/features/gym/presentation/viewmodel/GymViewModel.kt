package com.example.irozumi.features.gym.presentation.viewmodel

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irozumi.features.gym.data.repository.GymRepositoryImpl
import com.example.irozumi.features.gym.domain.model.AntiBlockTip
import com.example.irozumi.features.gym.domain.repository.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GymViewModel(
    private val repository: GymRepository = GymRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow(GymState())
    val state: StateFlow<GymState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getDailyExercise().collect { exercise ->
                _state.update { it.copy(dailyExercise = exercise) }
            }
        }
        viewModelScope.launch {
            repository.getAntiBlockTips().collect { tips ->
                _state.update { it.copy(antiBlockTips = tips) }
            }
        }
    }

    fun completeDailyExercise() {
        viewModelScope.launch {
            repository.completeExercise(_state.value.dailyExercise.id)
            _state.update { currentState ->
                currentState.copy(
                    currentStreakDays = currentState.currentStreakDays + 1,
                    badgeProgressPercentage = 1.0f
                )
            }
        }
    }

    fun addCustomTip(title: String, content: String) {
        if (content.isNotBlank()) {
            viewModelScope.launch {
                val newTip = AntiBlockTip(
                    id = _state.value.antiBlockTips.size + 1,
                    title = title,
                    content = content,
                    category = "Usuario" // Categoría por defecto
                )
                repository.addAntiBlockTip(newTip)
            }
        }
    }
}