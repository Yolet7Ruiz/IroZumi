package com.irozumi.features.gym.domain.model

data class AntiBlockTip(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val imageRes: Int? = null, // 💡 ID de la imagen/icono (ej: R.drawable.un_tip) o null si no tiene
    val author: String = "Sistema" // 💡 Para saber quién lo compartió
)