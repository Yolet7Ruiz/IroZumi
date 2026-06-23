package com.irozumi.features.gym.domain.repository

// Estas importaciones ya deberían funcionar si creaste los archivos arriba
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.AntiBlockTip
import kotlinx.coroutines.flow.Flow

interface GymRepository {
    fun getDailyExercise(): Flow<GymExercise>
    fun getAntiBlockTips(): Flow<List<AntiBlockTip>>
    suspend fun completeExercise(exerciseId: Int)
    suspend fun addAntiBlockTip(tip: AntiBlockTip)
}