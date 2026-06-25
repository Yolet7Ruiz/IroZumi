package com.irozumi.features.gym.domain.model

data class GymExercise(
    val id: Int,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val category: String, // "Líneas", "Círculos", "Sombras", "Perspectiva"
    val difficulty: String, // "Principiante", "Intermedio", "Avanzado"
    val isCompleted: Boolean = false,
    val pointsReward: Int = 10,
    val referenceImageRes: Int? = null // 💡 NUEVO: ID de la imagen de guía/referencia visual en tus drawables
)

data class UserStreak(
    val currentStreakDays: Int,
    val totalExercisesCompleted: Int,
    val totalPointsEarned: Int,
    val lastCompletedDateString: String? // Para validar si ya entrenó hoy
)