package com.irozumi.features.home.domain.model

data class ArtworkPost(
    val id: Int,                    // Cambiado de String a Int
    val title: String,
    val author: String,             // Renombrado para que coincida con tu ViewModel
    val imageUrl: String?,          // Para el valor null que envías en el constructor
    val category: String,           // Agregado para soportar filtros
    val description: String,
    val likesCount: Int,            // Renombrado para soportar toggleLike
    val comments: Int,
    val shares: Int,
    val isLikedByUser: Boolean = false // Flag por defecto requerido por tu ViewModel
)