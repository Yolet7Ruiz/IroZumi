package com.example.irozumi.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.irozumi.features.home.domain.model.ArtworkPost

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Lista maestra original para simular una base de datos local
    private var baseArtworksList = listOf(
        ArtworkPost(1, "Atardecer en Oaxaca", "LERONEZO JUNES M..", null, "Acuarela", "Acuarela sobre papel algodón. 30x40cm", 100, 100, 24),
        ArtworkPost(2, "Mi primer retrato serio", "Chupete", null, "Anime", "Lápiz 2B - Primer intento de retrato realista", 45, 100, 100),
        ArtworkPost(3, "Gore Conceptual", "Vamp_Ink", null, "Gore", "Estudio de sombras con tinta roja", 12, 67, 5),
        ArtworkPost(4, "Práctica Digital", "Kira_Art", null, "Anime", "Diseño de personaje original", 88, 230, 42)
    )

    init {
        _state.update { it.copy(artworks = baseArtworksList) }
    }

    fun onCategorySelected(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        filterArtworks()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterArtworks()
    }

    fun toggleLike(artworkId: Int) {
        baseArtworksList = baseArtworksList.map { artwork ->
            if (artwork.id == artworkId) {
                val newLikedStatus = !artwork.isLikedByUser
                artwork.copy(
                    isLikedByUser = newLikedStatus,
                    likesCount = if (newLikedStatus) artwork.likesCount + 1 else artwork.likesCount - 1
                )
            } else artwork
        }
        filterArtworks()
    }

    private fun filterArtworks() {
        val currentCategory = _state.value.selectedCategory
        val currentQuery = _state.value.searchQuery

        val filtered = baseArtworksList.filter { artwork ->
            val matchesCategory = currentCategory == "Todos" || artwork.category.equals(currentCategory, ignoreCase = true)
            val matchesQuery = currentQuery.isEmpty() ||
                    artwork.title.contains(currentQuery, ignoreCase = true) ||
                    artwork.author.contains(currentQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }

        _state.update { it.copy(artworks = filtered) }
    }
}