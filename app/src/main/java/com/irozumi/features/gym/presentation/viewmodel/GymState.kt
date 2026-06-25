package com.irozumi.features.gym.presentation.viewmodel

import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.AntiBlockTip

data class GymState(
    // 💡 CORREGIDO: Inicialización por defecto compatible con tu GymExercise real
    val dailyExercise: GymExercise = GymExercise(
        id = 0,
        title = "Cargando...",
        description = "",
        category = "General",
        difficulty = "Media",
        durationMinutes = 0,
        isCompleted = false
    ),
    val antiBlockTips: List<AntiBlockTip> = emptyList(),
    val isLoading: Boolean = false,
    val currentStreakDays: Int = 0,
    val badgeProgressPercentage: Float = 0f
)