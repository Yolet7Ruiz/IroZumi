package com.irozumi.features.gym.domain.model

data class AntiBlockTip(
    val id: Int, // Debe ser Int para que coincida con el ViewModel
    val title: String,
    val content: String,
    val category: String
)