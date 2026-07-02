package com.irozumi.features.messages.domain.model

import java.util.Date

// Modelo para la lista de contactos con los que se puede hablar (image_cac3bf.png)
data class MessageUser(
    val id: String,
    val name: String,
    val role: String, // Ejemplo: "Product Designer - Equipo UX", "Curadora"
    val email: String,
    val status: UserStatus,
    val profileImageUrl: String? = null,
    val isOnline: Boolean = false
)

// 💡 CORREGIDO: Se agregó 'class' después de 'enum'
enum class UserStatus {
    Activo, EnLinea, Disponible, Ocupado
}

// Modelo para los globos de texto dentro de una conversación (image_cac6c7.png)
data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Date,
    val isMine: Boolean
)