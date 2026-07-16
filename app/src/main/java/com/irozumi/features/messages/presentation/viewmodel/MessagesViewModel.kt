package com.irozumi.features.messages.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.messages.data.dataSource.MessagesRemoteDataSource
import com.irozumi.features.messages.domain.model.ChatMessage
import com.irozumi.features.messages.domain.model.MessageUser
import com.irozumi.features.messages.presentation.screens.MessagesUiState
import kotlinx.coroutines.launch
import java.util.Date
import com.irozumi.core.security.TokenManager

class MessagesViewModel : ViewModel() {

    private val dataSource = MessagesRemoteDataSource()

    var uiState by mutableStateOf<MessagesUiState>(MessagesUiState.Loading)
        private set

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                val remoteUsers = dataSource.getUsers()
                val users = remoteUsers.map { user ->
                    MessageUser(
                        id = user.id,
                        name = user.username,
                        role = "",
                        email = "",
                        status = com.irozumi.features.messages.domain.model.UserStatus.Activo
                    )
                }
                uiState = MessagesUiState.Success(users = users)
            } catch (e: Exception) {
                uiState = MessagesUiState.Success(users = emptyList())
            }
        }
    }

    fun selectUser(userId: String?) {
        val currentState = uiState
        if (currentState is MessagesUiState.Success) {
            uiState = currentState.copy(
                selectedUserId = userId,
                currentChatMessages = emptyList()  // Limpiar mensajes al cambiar de chat
            )
            if (userId != null) loadMessages(userId)
        }
    }

    private fun loadMessages(otherUserId: String) {
        viewModelScope.launch {
            try {
                val remoteMessages = dataSource.getMessages(otherUserId)
                val messages = remoteMessages.map { msg ->
                    ChatMessage(
                        id = msg.id,
                        senderId = msg.senderId,
                        text = msg.content,
                        timestamp = Date(),
                        isMine = msg.senderId == com.irozumi.core.security.TokenManager.currentUserId
                    )
                }
                val currentState = uiState
                if (currentState is MessagesUiState.Success) {
                    uiState = currentState.copy(currentChatMessages = messages)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sendMessage(text: String) {
        val currentState = uiState
        if (currentState !is MessagesUiState.Success || currentState.selectedUserId == null) return

        // Agregar a la lista local inmediatamente
        val newMessage = com.irozumi.features.messages.domain.model.ChatMessage(
            id = "", senderId = TokenManager.currentUserId, text = text,
            timestamp = java.util.Date(), isMine = true
        )
        val updatedMessages = currentState.currentChatMessages + newMessage
        uiState = currentState.copy(currentChatMessages = updatedMessages)

        // Enviar al backend en paralelo
        viewModelScope.launch {
            try {
                dataSource.sendMessage(currentState.selectedUserId!!, text)
            } catch (e: Exception) {
                // Si falla, quitar el mensaje
                uiState = currentState
            }
        }
    }
}
