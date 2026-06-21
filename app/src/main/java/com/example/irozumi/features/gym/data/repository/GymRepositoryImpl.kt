package com.example.irozumi.features.gym.data.repository

import com.example.irozumi.features.gym.domain.model.GymExercise
import com.example.irozumi.features.gym.domain.model.AntiBlockTip
import com.example.irozumi.features.gym.domain.repository.GymRepository
import com.example.irozumi.features.gym.data.datasource.GymSeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GymRepositoryImpl : GymRepository {

    // Ahora que GymSeedData existe, Kotlin ya no marcará error aquí
    private val _tipsState = MutableStateFlow(GymSeedData.initialTips)
    private val _dailyExerciseState = MutableStateFlow(GymSeedData.dailyExercise)

    override fun getDailyExercise(): Flow<GymExercise> = _dailyExerciseState.asStateFlow()

    override fun getAntiBlockTips(): Flow<List<AntiBlockTip>> = _tipsState.asStateFlow()

    // CORRECCIÓN: Se añade 'suspend' para que coincida con la interfaz
    override suspend fun completeExercise(exerciseId: Int) {
        _dailyExerciseState.update { it.copy(isCompleted = true) }
    }

    // CORRECCIÓN: Se añade 'suspend'
    override suspend fun addAntiBlockTip(tip: AntiBlockTip) {
        _tipsState.update { currentList -> currentList + tip }
    }
}