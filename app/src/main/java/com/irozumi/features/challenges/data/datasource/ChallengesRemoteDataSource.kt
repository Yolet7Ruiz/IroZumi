package com.irozumi.features.challenges.data.datasource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChallengeRemote(
    val id: String, val title: String, val description: String?,
    val theme: String?, val startDate: String, val endDate: String,
    val status: String, val createdAt: String
)

@Serializable
data class SubmissionRemote(
    val id: String, val challengeId: String, val userId: String,
    val username: String, val title: String?, val imageUrl: String?,
    val category: String?, val votes: Int, val createdAt: String
)

class ChallengesRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }
    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getActiveChallenges(): List<ChallengeRemote> {
        val response = client.get("$baseUrl/api/v1/challenges") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar retos")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getSubmissions(challengeId: String): List<SubmissionRemote> {
        val response = client.get("$baseUrl/api/v1/challenges/$challengeId/submissions") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar participantes")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun submitArtwork(challengeId: String, title: String, category: String, imageBase64: String) {
        val response = client.post("$baseUrl/api/v1/challenges/submit") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf("challengeId" to challengeId, "title" to title, "category" to category, "imageUrl" to imageBase64))
        }
        if (response.status.value != 201) throw Exception("Error al participar")
    }

    suspend fun voteSubmission(submissionId: String) {
        val response = client.post("$baseUrl/api/v1/challenges/submissions/$submissionId/vote") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al votar")
    }
    suspend fun createChallenge(title: String, description: String, startDate: String, endDate: String, theme: String, imageBase64: String) {
        val response = client.post("$baseUrl/api/v1/challenges") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf("title" to title, "description" to description, "theme" to theme, "startDate" to startDate, "endDate" to endDate))
        }
        if (response.status.value != 201) throw Exception("Error al crear reto")
    }
}