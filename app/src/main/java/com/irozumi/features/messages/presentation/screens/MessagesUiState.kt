package com.irozumi.features.messages.presentation.screens

// Usamos mapas o estructuras nativas para simular la información de tus capturas
sealed interface MessagesUiState {
    object Loading : MessagesUiState

    data class Success(
        val users: List<Map<String, Any>>,
        val selectedUserId: String? = null,
        val currentChatMessages: List<Map<String, Any>> = emptyList()
    ) : MessagesUiState
}