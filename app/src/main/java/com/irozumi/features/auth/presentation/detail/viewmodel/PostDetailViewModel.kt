package com.irozumi.features.home.presentation.detail.viewmodel

import androidx.lifecycle.ViewModel
import com.irozumi.features.home.domain.model.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    init {
        loadPostDetails()
    }

    private fun loadPostDetails() {
        _state.update {
            it.copy(
                title = "Atardecer en Oaxaca",
                authorName = "LERONEZO JUNES M.",
                description = "Acuarela sobre papel algodón • 30cm × 40cm • 2026",
                comments = listOf(
                    Comment("1", "@BandaRomantica24", "Increíble manejo de la luz y las sombras en el papel.", "3 meses")
                )
            )
        }
    }

    fun toggleLike() {
        _state.update { currentState ->
            val newLiked = !currentState.isLiked
            currentState.copy(
                isLiked = newLiked,
                likesCount = if (newLiked) currentState.likesCount + 1 else currentState.likesCount - 1
            )
        }
    }

    fun toggleFollow() {
        _state.update { it.copy(isFollowing = !it.isFollowing) }
    }

    fun addComment(text: String) {
        if (text.isBlank()) return

        val newComment = Comment(
            id = System.currentTimeMillis().toString(),
            authorName = "@TuUsuarioArtista",
            text = text,
            timestamp = "Justo ahora"
        )

        _state.update { it.copy(comments = it.comments + newComment) }
    }
}