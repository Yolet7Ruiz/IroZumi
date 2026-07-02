package com.irozumi.features.messages.domain.repository

import com.irozumi.features.messages.domain.model.ChatMessage
import com.irozumi.features.messages.domain.model.MessageUser
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun getUsersList(): Flow<List<MessageUser>>
    fun getChatMessages(userId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(receiverId: String, text: String)
}