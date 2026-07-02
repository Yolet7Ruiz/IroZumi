package com.irozumi.features.catalog.domain.model

import android.net.Uri

data class ArtworkProduct(
    val id: String,
    val title: String,
    val artistName: String,
    val price: Double,
    val rating: Double,
    val imageUri: Uri? = null,
    val category: String,
    val isFavorite: Boolean = false
)