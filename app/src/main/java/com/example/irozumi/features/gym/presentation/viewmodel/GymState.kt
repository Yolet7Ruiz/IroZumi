package com.example.irozumi.features.gym.presentation.viewmodel

import com.example.irozumi.features.gym.domain.model.AntiBlockTip
import com.example.irozumi.features.gym.domain.model.GymExercise

data class GymState(
    val dailyExercise: GymExercise = GymExercise(0, "", "", 0),
    val antiBlockTips: List<AntiBlockTip> = emptyList(),
    val currentStreakDays: Int = 0,
    val badgeProgressPercentage: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)