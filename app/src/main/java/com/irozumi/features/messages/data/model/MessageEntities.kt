package com.irozumi.features.messages.data.model

import com.irozumi.core.security.TokenManager

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val content: String,
    val createdAt: String = ""
) {
    val isMine: Boolean get() = senderId == TokenManager.currentUserId
}

data class ChatUser(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val lastMessage: String? = null
)