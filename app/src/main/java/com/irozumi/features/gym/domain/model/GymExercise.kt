package com.irozumi.features.gym.domain.model

data class GymExercise(
    val id: String,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false
)