package com.irozumi.features.gym.data.datasource

import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.model.GymExercise

object GymSeedData {
    val dailyExercise = GymExercise(
        id = "1", // CORREGIDO: id como String
        name = "Estiramiento Creativo",
        description = "Tómate 5 minutos para estirar los brazos y cerrar los ojos.",
        isCompleted = false // Agregamos la propiedad real de tu GymExercise
    )

    val initialTips = listOf(
        AntiBlockTip(1, "Regla de los 10 minutos", "Escribe cualquier cosa sin parar por 10 min.", "Creatividad"),
        AntiBlockTip(2, "Caminata rápida", "Camina un poco para oxigenar el cerebro.", "Físico")
    )
}