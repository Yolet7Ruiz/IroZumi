package com.irozumi.features.catalog.presentation.screens

import com.irozumi.features.catalog.domain.model.ArtworkProduct

sealed interface CatalogUiState {
    object Loading : CatalogUiState

    data class Success(
        val products: List<ArtworkProduct> = emptyList(),
        val selectedCategory: String = "Todos"
    ) : CatalogUiState

    data class Error(val message: String) : CatalogUiState
}