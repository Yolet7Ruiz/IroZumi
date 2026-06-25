package com.irozumi.features.challenges.domain.model

data class MaterialTip(
    val id: Int,
    val materialName: String,   // Ej: "Estilógrafos Sakura Pigma Micron"
    val category: String,       // Ej: "Delineado", "Papel", "Lápices"
    val approximatePrice: String, // Ej: "$3.50 USD" o aproximado accesible
    val reviewDescription: String, // Por qué es bueno y económico
    val authorUsername: String, // Quién dejó el tip
    val ratingStars: Int,        // Puntuación de calidad (1 a 5)
    val likesCount: Int = 0
)