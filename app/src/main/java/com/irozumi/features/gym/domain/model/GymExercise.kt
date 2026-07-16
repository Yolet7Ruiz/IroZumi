package com.irozumi.features.gym.domain.model

data class GymExercise(
    val id: String,                    // UUID del backend
    val title: String,
    val description: String? = null,
    val durationMinutes: Int,
    val category: String,
    val difficulty: String,
    val isCompleted: Boolean = false,
    val pointsReward: Int = 10,
    val imageUrl: String? = null,      // URL de Cloudinary (antes referenceImageRes: Int?)
    val createdBy: String? = null,
    val createdAt: String? = null
)

data class UserStreak(
    val currentStreakDays: Int,
    val totalExercisesCompleted: Int,
    val totalPointsEarned: Int,
    val lastCompletedDateString: String?
)