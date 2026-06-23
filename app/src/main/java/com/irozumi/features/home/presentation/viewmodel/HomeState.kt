package com.irozumi.features.home.presentation.viewmodel

import com.irozumi.features.home.domain.model.ArtworkPost

data class HomeState(
    val artworks: List<ArtworkPost> = emptyList(),
    val categories: List<String> = listOf("Todos", "Anime", "Acuarela", "Gore", "Realista"),
    val selectedCategory: String = "Todos",
    val searchQuery: String = ""
)