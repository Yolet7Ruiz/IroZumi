package com.irozumi.features.gym.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.gym.data.repository.GymRepositoryImpl
import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.repository.GymRepository
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
            // SOLUCIÓN: Convertimos el String ID a Int para que coincida con el repositorio
            val exerciseIdInt = _state.value.dailyExercise.id.toIntOrNull() ?: 0

            repository.completeExercise(exerciseIdInt)
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
                    category = "Usuario"
                )
                repository.addAntiBlockTip(newTip)
                loadData()
            }
        }
    }
}