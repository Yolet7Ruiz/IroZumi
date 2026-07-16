package com.irozumi.features.messages.data.repository

import com.irozumi.core.security.TokenManager
import com.irozumi.features.messages.data.dataSource.MessagesRemoteDataSource
import com.irozumi.features.messages.domain.model.ChatMessage
import com.irozumi.features.messages.domain.model.MessageUser
import com.irozumi.features.messages.domain.model.UserStatus
import com.irozumi.features.messages.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class MessageRepositoryImpl(
    private val remoteDataSource: MessagesRemoteDataSource = MessagesRemoteDataSource()
) : MessagesRepository {

    override fun getUsersList(): Flow<List<MessageUser>> = flow {
        try {
            val response = remoteDataSource.getUsers()
            val users = response.map { res ->
                MessageUser(
                    id = res.id,
                    name = res.username,
                    role = "Miembro de IroZumi",
                    email = "",
                    status = UserStatus.Activo,
                    profileImageUrl = res.avatarUrl,
                    isOnline = true
                )
            }
            emit(users)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getChatMessages(userId: String): Flow<List<ChatMessage>> = flow {
        try {
            val response = remoteDataSource.getMessages(userId)
            val currentUserId = TokenManager.currentUserId
            val messages = response.map { res ->
                ChatMessage(
                    id = res.id,
                    senderId = res.senderId,
                    text = res.content,
                    timestamp = Date(), // Aquí podrías parsear res.createdAt si fuera necesario
                    isMine = res.senderId == currentUserId
                )
            }
            emit(messages)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun sendMessage(receiverId: String, text: String) {
        try {
            remoteDataSource.sendMessage(receiverId, text)
        } catch (e: Exception) {
            // Manejar error de envío si es necesario
            throw e
        }
    }
}
