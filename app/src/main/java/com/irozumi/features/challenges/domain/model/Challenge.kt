package com.irozumi.features.challenges.domain.model

import java.util.Date

enum class ChallengeStatus {
    PROXIMO,  // Próximo reto (Ej: Jueves de reto)
    ACTIVO,   // En vivo, permite subir dibujo (Ej: Sábado de estilo libre)
    CERRADO   // Ya finalizó y está en fase de votación/resultados
}

data class Challenge(
    val id: String,
    val title: String,          // Ej: "Martes de boceto", "Jueves de reto"
    val concept: String,        // Ej: "Manos en movimiento", "Luz y sombra"
    val description: String,    // Descripción detallada de las reglas
    val status: ChallengeStatus,
    val endDate: Date,          // Hasta qué día tienen para subir sus dibujos
    val participantsCount: Int,
    val referenceImageRes: Int? = null, // Imagen de guía del concepto
    val isUserParticipating: Boolean = false
)