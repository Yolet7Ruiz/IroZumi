package com.irozumi.features.home.domain.model

data class ArtworkPost(
    val id: Int,
    val title: String,
    val author: String,
    val authorAvatarUrl: String? = null,
    val category: String,
    val description: String = "",
    val sharesCount: Int = 100,
    val likesCount: Int,
    val commentsCount: Int,
    val isLikedByUser: Boolean = false,
    val imageResId: Int? = null // Para las pruebas usaremos IDs de recursos locales
)
