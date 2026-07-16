package com.irozumi.features.messages.presentation.screens

import com.irozumi.features.messages.domain.model.ChatMessage
import com.irozumi.features.messages.domain.model.MessageUser

sealed interface MessagesUiState {
    object Loading : MessagesUiState
    data class Success(
        val users: List<MessageUser> = emptyList(),
        val selectedUserId: String? = null,
        val currentChatMessages: List<ChatMessage> = emptyList()
    ) : MessagesUiState
}