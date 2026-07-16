package com.irozumi.features.gym.data.repository

import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.UserStreak
import com.irozumi.features.gym.domain.repository.GymRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GymRepositoryImpl : GymRepository {

    private val _exercises = MutableStateFlow<List<GymExercise>>(emptyList())

    override fun getExercises(): Flow<List<GymExercise>> = _exercises

    override fun getUserStreak(): Flow<UserStreak> = MutableStateFlow(
        UserStreak(currentStreakDays = 0, totalExercisesCompleted = 0, totalPointsEarned = 0, lastCompletedDateString = null)
    )

    override suspend fun completeExercise(exerciseId: Int) {
        // Ya no se usa, se maneja en GymViewModel con GymRemoteDataSource
    }

    override fun getAntiBlockTips(): List<String> = listOf(
        "Si te bloqueas, sal a caminar 10 minutos sin celular.",
        "Cambia de herramienta: digital → lápiz físico.",
        "No busques la perfección; entrena la memoria muscular.",
        "Pon música instrumental de fondo.",
        "Dibuja con tu mano no dominante."
    )
}