package com.irozumi.features.gym.domain.repository

import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.UserStreak
import kotlinx.coroutines.flow.Flow

interface GymRepository {
    fun getExercises(): Flow<List<GymExercise>>
    fun getUserStreak(): Flow<UserStreak>
    suspend fun completeExercise(exerciseId: Int)
    fun getAntiBlockTips(): List<String>
}