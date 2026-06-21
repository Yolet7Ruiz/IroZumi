package com.example.irozumi.features.gym.domain.model

data class GymExercise(
    val id: Int,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val isCompleted: Boolean = false
)