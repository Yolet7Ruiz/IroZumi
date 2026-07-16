package com.irozumi.features.gym.data.datasource

import com.irozumi.features.gym.domain.model.AntiBlockTip

object GymSeedData {
    val initialCommunityTips = listOf(
        AntiBlockTip(1, "Regla de los 10 minutos", "Escribe o dibuja cualquier cosa sin parar por 10 min.", "Creatividad"),
        AntiBlockTip(2, "Caminata rápida", "Camina un poco para oxigenar el cerebro.", "Físico"),
        AntiBlockTip(3, "Cambio de lienzo", "Prueba dibujar en un fondo oscuro en vez de blanco.", "Digital", author = "ArteMaster99")
    )
}