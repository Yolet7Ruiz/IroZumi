package com.irozumi.features.home.data.dataSource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import com.irozumi.features.home.domain.model.ArtworkPost
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
data class PostResponse(
    val id: String,
    val title: String,
    val imageUrl: String? = null,
    val technique: String? = null,
    val dimensions: String? = null,
    val material: String? = null,
    val style: String? = null,
    val description: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isForSale: Boolean = false,
    val createdAt: String? = null,
    val author: AuthorResponse? = null
)

@Serializable
data class AuthorResponse(
    val id: String,
    val username: String,
    val avatarUrl: String?
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val description: String,
    val style: String,
    val imageBase64: String
)

@Serializable
data class AddCommentRequest(val content: String)

@Serializable
data class CommentResponse(
    val id: String,
    val content: String,
    val author: AuthorResponse,
    val createdAt: String
)

@Serializable
data class ErrorResponse(val message: String)

@Serializable
data class TokenResponse(val accessToken: String, val refreshToken: String)

class HomeRemoteDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }

    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun refreshTokenIfNeeded() {
        if (TokenManager.refreshToken.isNotBlank()) {
            try {
                val response = client.post("$baseUrl/api/v1/auth/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("refreshToken" to TokenManager.refreshToken))
                }
                if (response.status.value == 200) {
                    val tokenResponse = json.decodeFromString<TokenResponse>(response.bodyAsText())
                    TokenManager.saveTokens(tokenResponse.accessToken, tokenResponse.refreshToken)
                }
            } catch (e: Exception) {
                TokenManager.clearTokens()
            }
        }
    }
    suspend fun getPosts(category: String, query: String): List<ArtworkPost> {
        refreshTokenIfNeeded()
        val url = buildString {
            append("$baseUrl/api/v1/gallery/posts")
            if (category != "Todos") append("?style=$category")
        }
        val response = client.get(url) { header("Authorization", TokenManager.getAuthHeader()) }
        if (response.status.value != 200) throw Exception("Error al obtener posts")
        return json.decodeFromString<List<PostResponse>>(response.bodyAsText()).map { it.toDomain() }
    }

    suspend fun toggleLike(postId: String): PostResponse {
        refreshTokenIfNeeded()
        val response = client.post("$baseUrl/api/v1/gallery/posts/$postId/like") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al dar like")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getComments(postId: String): List<CommentResponse> {
        refreshTokenIfNeeded()
        val response = client.get("$baseUrl/api/v1/gallery/posts/$postId/comments") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar comentarios")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun getTopArtists(): List<AuthorResponse> {
        refreshTokenIfNeeded()
        val response = client.get("$baseUrl/api/v1/gallery/users/top-artists") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar artistas")
        return json.decodeFromString(response.bodyAsText())
    }
    suspend fun postComment(postId: String, content: String): CommentResponse {
        refreshTokenIfNeeded()
        val response = client.post("$baseUrl/api/v1/gallery/posts/$postId/comments") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(AddCommentRequest(content))
        }
        if (response.status.value == 401) {
            refreshTokenIfNeeded()
            val retryResponse = client.post("$baseUrl/api/v1/gallery/posts/$postId/comments") {
                contentType(ContentType.Application.Json)
                header("Authorization", TokenManager.getAuthHeader())
                setBody(AddCommentRequest(content))
            }
            if (retryResponse.status.value != 201 && retryResponse.status.value != 200) {
                throw Exception("Error al enviar comentario")
            }
            return json.decodeFromString(retryResponse.bodyAsText())
        }
        if (response.status.value != 201 && response.status.value != 200) {
            throw Exception(try { json.decodeFromString<ErrorResponse>(response.bodyAsText()).message } catch (e: Exception) { "Error al enviar" })
        }
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun createPost(title: String, description: String, category: String, imageBase64: String): PostResponse {
        refreshTokenIfNeeded()
        val response = client.post("$baseUrl/api/v1/gallery/posts") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(CreatePostRequest(title, description, category, imageBase64))
        }
        if (response.status.value != 201) throw Exception("Error al crear publicación")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun deletePost(postId: String) {
        refreshTokenIfNeeded()
        val response = client.delete("$baseUrl/api/v1/gallery/posts/$postId") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al eliminar")
    }

    private fun PostResponse.toDomain() = ArtworkPost(
        id = id, title = title, author = author?.username ?: "Desconocido",
        imageUrl = imageUrl, category = style ?: "Desconocido", description = description ?: "",
        likesCount = likesCount, comments = commentsCount, shares = sharesCount
    )
}