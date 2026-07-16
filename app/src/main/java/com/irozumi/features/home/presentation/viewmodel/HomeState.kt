package com.irozumi.features.home.presentation.viewmodel

import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.domain.model.Comment

data class HomeState(
    val artworks: List<ArtworkPost> = emptyList(),
    val categories: List<String> = listOf("Todos", "Anime", "Acuarela", "Gore", "Realismo", "Digital"),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activePostForComments: ArtworkPost? = null,
    val comments: List<Comment> = emptyList(),
    val isCommentsLoading: Boolean = false,
    val commentError: String? = null // Error específico para comentarios
)
