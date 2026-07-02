package com.irozumi.features.messages.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.irozumi.features.messages.presentation.screens.MessagesUiState

class MessagesViewModel : ViewModel() {

    private val mockUsers: List<Map<String, Any>> = listOf(
        mapOf("id" to "1", "name" to "María González", "role" to "Product Designer · Equipo UX", "email" to "maria@empresa.com", "status" to "Activo", "isOnline" to true),
        mapOf("id" to "2", "name" to "Carlos Rivera", "role" to "Frontend Engineer · Plataforma", "email" to "carlos@empresa.com", "status" to "En línea", "isOnline" to false),
        mapOf("id" to "3", "name" to "Lucía Fernández", "role" to "Community Manager · Marketing", "email" to "lucia@empresa.com", "status" to "Disponible", "isOnline" to false),
        mapOf("id" to "4", "name" to "Andrés Molina", "role" to "Data Analyst · BI", "email" to "andres@empresa.com", "status" to "Ocupado", "isOnline" to false),
        mapOf("id" to "5", "name" to "Sofía Torres", "role" to "HR Specialist · Personas", "email" to "sofia@empresa.com", "status" to "Activo", "isOnline" to false)
    )

    private val mockMessages: SnapshotStateList<Map<String, Any>> = mutableStateListOf(
        mapOf("text" to "Hola, ¿te gustaría ver las piezas nuevas de la colección?", "isMine" to false),
        mapOf("text" to "Sí, muéstrame las obras destacadas y sus detalles.", "isMine" to true),
        mapOf("text" to "Claro, aquí tienes una vista tipo chat con perfil y nombres visibles.", "isMine" to false),
        mapOf("text" to "Perfecto, quiero que se vea elegante y limpio.", "isMine" to true)
    )

    var uiState by mutableStateOf<MessagesUiState>(
        MessagesUiState.Success(users = mockUsers, selectedUserId = null, currentChatMessages = mockMessages.toList())
    )
        private set

    fun selectUser(userId: String?) {
        val currentState = uiState
        if (currentState is MessagesUiState.Success) {
            uiState = currentState.copy(selectedUserId = userId)
        }
    }

    fun sendMessage(text: String) {
        if (text.isNotBlank()) {
            mockMessages.add(mapOf("text" to text, "isMine" to true))
            val currentState = uiState
            if (currentState is MessagesUiState.Success) {
                uiState = currentState.copy(currentChatMessages = mockMessages.toList())
            }
        }
    }
}