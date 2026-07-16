package com.irozumi.features.notifications.data.datasource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.request.put

@Serializable
data class NotificationRemote(
    val id: String,
    val type: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String,
    val username: String
)

class NotificationsRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }
    private val baseUrl = Config.BASE_URL

    suspend fun deleteNotification(id: String) {
        val response = client.delete("$baseUrl/api/v1/notifications/$id") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (!response.status.isSuccess()) throw Exception("Error al eliminar")
    }

    suspend fun getNotifications(): List<NotificationRemote> {
        val response = client.get("$baseUrl/api/v1/notifications") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (!response.status.isSuccess()) throw Exception("Error al cargar")
        return response.body()
    }

    suspend fun markAsRead(id: String) {
        val response = client.put("$baseUrl/api/v1/notifications/$id/read") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (!response.status.isSuccess()) throw Exception("Error al silenciar")
    }
}