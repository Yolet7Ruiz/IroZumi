package com.irozumi.features.gym.presentation.viewmodel

import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.AntiBlockTip

data class GymState(
    val dailyExercise: GymExercise = GymExercise("0", "Cargando...", ""),
    val antiBlockTips: List<AntiBlockTip> = emptyList(),
    val isLoading: Boolean = false,
    val currentStreakDays: Int = 0,               // Agregado
    val badgeProgressPercentage: Float = 0f      // Agregado
)