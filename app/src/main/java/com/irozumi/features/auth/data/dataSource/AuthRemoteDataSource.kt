package com.irozumi.features.auth.data.dataSource

import com.irozumi.core.config.Config
import com.irozumi.features.auth.domain.model.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String, val artisticLevel: String = "Principiante")

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

@Serializable
data class ErrorResponse(val message: String)

class AuthRemoteDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loginWithApi(email: String, password: String): UserSession {
        val response = client.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        val responseBody = response.bodyAsText()
        if (response.status.value != 200) {
            val errorResponse = try {
                json.decodeFromString<ErrorResponse>(responseBody)
            } catch (e: Exception) {
                ErrorResponse("No pudimos conectar con el servidor. Revisa tu conexión a internet.")
            }
            throw Exception(errorResponse.message)
        }
        val authResponse = json.decodeFromString<AuthResponse>(responseBody)
        return UserSession(
            token = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            username = authResponse.user.username,
            email = authResponse.user.email,
            userId = authResponse.user.id,
            role = authResponse.user.role
        )
    }

    suspend fun registerWithApi(username: String, email: String, password: String, artisticLevel: String): UserSession {
        val response = client.post("$baseUrl/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, email, password, artisticLevel))
        }
        val responseBody = response.bodyAsText()
        if (response.status.value != 201) {
            val errorResponse = try {
                json.decodeFromString<ErrorResponse>(responseBody)
            } catch (e: Exception) {
                ErrorResponse("No pudimos conectar con el servidor. Revisa tu conexión a internet.")
            }
            throw Exception(errorResponse.message)
        }
        val authResponse = json.decodeFromString<AuthResponse>(responseBody)
        return UserSession(
            token = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            username = authResponse.user.username,
            email = authResponse.user.email,
            userId = authResponse.user.id,
            role = authResponse.user.role
        )
    }
}