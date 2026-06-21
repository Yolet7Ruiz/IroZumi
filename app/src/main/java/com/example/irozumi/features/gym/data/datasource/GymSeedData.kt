package com.example.irozumi.features.gym.data.datasource

import com.example.irozumi.features.gym.domain.model.AntiBlockTip
import com.example.irozumi.features.gym.domain.model.GymExercise

object GymSeedData {
    val dailyExercise = GymExercise(
        id = 1,
        name = "Estiramiento Creativo",
        description = "Tómate 5 minutos para estirar los brazos y cerrar los ojos.",
        durationMinutes = 5
    )

    val initialTips = listOf(
        AntiBlockTip(1, "Regla de los 10 minutos", "Escribe cualquier cosa sin parar por 10 min.", "Creatividad"),
        AntiBlockTip(2, "Caminata rápida", "Camina un poco para oxigenar el cerebro.", "Físico")
    )
}