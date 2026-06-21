package com.example.irozumi.features.gym.domain.model

data class AntiBlockTip(
    val id: Int,
    val title: String,
    val content: String,
    val category: String // Ej: "Creatividad", "Mental", "Físico"
)