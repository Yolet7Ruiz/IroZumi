package com.irozumi.features.gym.data.datasource

import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.model.GymExercise

object GymSeedData {
    // 💡 CORREGIDO: Estructura adaptada perfectamente a tu nuevo GymExercise
    val dailyExercise = GymExercise(
        id = 1,
        title = "Estiramiento Creativo",
        description = "Tómate 5 minutos para estirar los brazos y cerrar los ojos.",
        durationMinutes = 5,
        category = "Líneas", // Actualizado a tus categorías reales
        difficulty = "Principiante", // Actualizado a tus dificultades reales
        isCompleted = false,
        pointsReward = 15, // Tus puntos personalizados
        referenceImageRes = android.R.drawable.ic_menu_gallery // 💡 Imagen de referencia (usa R.drawable.tu_imagen en producción)
    )

    val initialCommunityTips = listOf(
        AntiBlockTip(1, "Regla de los 10 minutos", "Escribe o dibuja cualquier cosa sin parar por 10 min.", "Creatividad"),
        AntiBlockTip(2, "Caminata rápida", "Camina un poco para oxigenar el cerebro.", "Físico"),
        AntiBlockTip(3, "Cambio de lienzo", "Prueba dibujar en un fondo oscuro en vez de blanco.", "Digital", author = "ArteMaster99")
    )
}