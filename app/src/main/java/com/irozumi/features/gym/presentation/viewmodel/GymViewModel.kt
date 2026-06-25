package com.irozumi.features.gym.presentation.viewmodel

import android.net.Uri // 💡 Agregado para soportar las URIs de los dibujos subidos
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.gym.data.datasource.GymSeedData
import com.irozumi.features.gym.data.repository.GymRepositoryImpl
import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.UserStreak
import com.irozumi.features.gym.domain.repository.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GymViewModel(
    private val repository: GymRepository = GymRepositoryImpl()
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<GymExercise>>(emptyList())
    val exercises: StateFlow<List<GymExercise>> = _exercises.asStateFlow()

    // 💡 La racha inicia estrictamente en cero (0)
    private val _streak = MutableStateFlow<UserStreak?>(
        UserStreak(
            currentStreakDays = 0,
            totalPointsEarned = 0,
            totalExercisesCompleted = 0,
            lastCompletedDateString = ""
        )
    )
    val streak: StateFlow<UserStreak?> = _streak.asStateFlow()

    private val _currentTip = MutableStateFlow<AntiBlockTip?>(null)
    val currentTip: StateFlow<AntiBlockTip?> = _currentTip.asStateFlow()

    // Manejamos el estado mutable aquí en el ViewModel usando la semilla limpia del SeedData
    private val _communityTips = mutableStateListOf<AntiBlockTip>().apply {
        addAll(GymSeedData.initialCommunityTips)
    }
    val communityTipsList: List<AntiBlockTip> get() = _communityTips

    init {
        loadData()
        getRandomTip()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getExercises().collect { _exercises.value = it }
        }
    }

    // 💡 MODIFICADO: Ahora el ejercicio se completa únicamente cuando el usuario sube la foto de su dibujo
    fun completeExerciseWithPhoto(id: Int, photoUri: Uri) {
        viewModelScope.launch {
            repository.completeExercise(id)
            val current = _streak.value
            if (current != null) {
                _streak.value = current.copy(
                    currentStreakDays = current.currentStreakDays + 1,
                    totalPointsEarned = current.totalPointsEarned + 15,
                    totalExercisesCompleted = current.totalExercisesCompleted + 1,
                    lastCompletedDateString = photoUri.toString() // Guardamos la URI como comprobante
                )
            }
        }
    }

    // Mantengo esta función por si necesitas llamadas directas de pruebas, pero en la UI usarás la de arriba
    fun completeExercise(id: Int) {
        viewModelScope.launch {
            repository.completeExercise(id)
            val current = _streak.value
            if (current != null) {
                _streak.value = current.copy(
                    currentStreakDays = current.currentStreakDays + 1,
                    totalPointsEarned = current.totalPointsEarned + 15,
                    totalExercisesCompleted = current.totalExercisesCompleted + 1
                )
            }
        }
    }

    fun resetStreak() {
        val current = _streak.value
        if (current != null) {
            _streak.value = current.copy(currentStreakDays = 0)
        }
    }

    fun getRandomTip() {
        if (_communityTips.isNotEmpty()) {
            _currentTip.value = _communityTips.random()
        }
    }

    fun publishCommunityTip(title: String, description: String, category: String) {
        if (title.isNotBlank() && description.isNotBlank()) {
            val newTip = AntiBlockTip(
                id = _communityTips.size + 1,
                title = title,
                description = description,
                category = category,
                author = "Tú (Artista)"
            )
            _communityTips.add(0, newTip) // Agrega al inicio de la lista local
            _currentTip.value = newTip
        }
    }
}