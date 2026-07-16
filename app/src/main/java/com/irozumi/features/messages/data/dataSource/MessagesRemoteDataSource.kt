package com.irozumi.features.messages.data.dataSource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val avatarUrl: String?
)

@Serializable
data class MessageResponse(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val content: String,
    val createdAt: String
)

class MessagesRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getUsers(): List<UserResponse> {
        val response = client.get("$baseUrl/api/v1/users") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar usuarios")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getMessages(otherUserId: String): List<MessageResponse> {
        val response = client.get("$baseUrl/api/v1/messages/$otherUserId") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar mensajes")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun sendMessage(receiverId: String, content: String): MessageResponse {
        val response = client.post("$baseUrl/api/v1/messages") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf("receiverId" to receiverId, "content" to content))
        }
        if (response.status.value != 201) throw Exception("Error al enviar mensaje")
        return json.decodeFromString(response.bodyAsText())
    }
}
