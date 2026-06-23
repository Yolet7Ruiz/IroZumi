package com.irozumi.features.auth.data.dataSource

import com.irozumi.features.auth.domain.model.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val role: String,
    val isVerified: Boolean,
    val isBeginnerProtected: Boolean
)

class AuthRemoteDataSource {

    private val client = HttpClient()
    private val baseUrl = "http://10.0.2.2:8080" // Emulador Android -> localhost
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loginWithApi(email: String, password: String): UserSession {
        val response = client.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        val authResponse = json.decodeFromString<AuthResponse>(response.body())
        return UserSession(
            token = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            username = authResponse.user.username,
            email = authResponse.user.email
        )
    }

    suspend fun registerWithApi(username: String, email: String, password: String): UserSession {
        val response = client.post("$baseUrl/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, email, password))
        }
        val authResponse = json.decodeFromString<AuthResponse>(response.body())
        return UserSession(
            token = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            username = authResponse.user.username,
            email = authResponse.user.email
        )
    }
}