package com.irozumi.features.gym.data.repository

import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.UserStreak
import com.irozumi.features.gym.domain.repository.GymRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class GymRepositoryImpl : GymRepository {

    private val exercisesList = MutableStateFlow(
        listOf(
            GymExercise(1, "Líneas Paralelas Infinitas", "Dibuja líneas verticales paralelas lo más juntas posible sin que se toquen. Ideal para el control de presión.", 5, "Líneas", "Principiante"),
            GymExercise(2, "Círculos Concéntricos", "Crea círculos perfectos uno dentro de otro manteniendo la misma distancia. Mejora el pivote de la muñeca.", 8, "Círculos", "Intermedio"),
            GymExercise(3, "Degradado de Tramas (Gore/Dark)", "Crea una escala de oscuridad usando solo líneas cruzadas de más densas a más separadas.", 10, "Sombras", "Avanzado"),
            GymExercise(4, "Perspectiva de 2 Puntos", "Dibuja 5 cubos flotando orientados a dos puntos de fuga diferentes en el horizonte.", 12, "Perspectiva", "Intermedio"),
            GymExercise(5, "Elipses en Cilindro", "Dibuja cilindros en diferentes ángulos cuidando la curvatura de las elipses internas.", 7, "Círculos", "Principiante")
        )
    )

    private val userStreakState = MutableStateFlow(
        UserStreak(
            currentStreakDays = 4, // El usuario lleva 4 días seguidos
            totalExercisesCompleted = 14,
            totalPointsEarned = 140,
            lastCompletedDateString = null
        )
    )

    override fun getExercises(): Flow<List<GymExercise>> = exercisesList

    override fun getUserStreak(): Flow<UserStreak> = userStreakState

    override suspend fun completeExercise(exerciseId: Int) {
        // Actualizar el ejercicio a completado
        val currentList = exercisesList.value.map { exercise ->
            if (exercise.id == exerciseId && !exercise.isCompleted) {
                exercise.copy(isCompleted = true)
            } else {
                exercise
            }
        }
        exercisesList.value = currentList

        // Sumar puntos y racha si corresponde
        val pointsToAdd = exercisesList.value.find { it.id == exerciseId }?.pointsReward ?: 0
        val currentStreak = userStreakState.value
        userStreakState.value = currentStreak.copy(
            totalExercisesCompleted = currentStreak.totalExercisesCompleted + 1,
            totalPointsEarned = currentStreak.totalPointsEarned + pointsToAdd
            // Aquí se podría aumentar la racha si es un día nuevo
        )
    }

    override fun getAntiBlockTips(): List<String> = listOf(
        "Si te bloqueas, sal a caminar 10 minutos sin celular. Volverás con la mente fresca.",
        "Cambia de herramienta: si dibujas en digital, toma un lápiz físico por 5 minutos.",
        "No busques la perfección en el Gym; el objetivo es entrenar la memoria de tus músculos.",
        "Pon música instrumental o ambiental de fondo para activar el hemisferio creativo.",
        "Prueba dibujar algo con tu mano no dominante para forzar nuevas conexiones visuales."
    )
}