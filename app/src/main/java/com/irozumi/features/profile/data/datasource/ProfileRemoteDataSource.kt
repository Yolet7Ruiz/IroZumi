package com.irozumi.features.profile.data.datasource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.statement.bodyAsText
@Serializable
data class ProfileRemote(
    val id: String, val username: String, val displayName: String,
    val bio: String?, val avatarUrl: String?, val profilePictureUrl: String?,
    val coverPictureUrl: String?, val instagram: String?, val twitter: String?,
    val role: String, val postsCount: Int, val followersCount: Int, val followingCount: Int
)

@Serializable
data class PostRemote(
    val id: String, val title: String, val description: String?,
    val style: String?, val likesCount: Int, val commentsCount: Int,
    val imageUrl: String?,
    val isLiked: Boolean = false
)

@Serializable
data class CommentRemote(
    val id: String,
    val content: String,
    val author: AuthorRemote,
    val createdAt: String
)
@Serializable
data class AuthorRemote(
    val id: String,
    val username: String,
    val avatarUrl: String?
)
class ProfileRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }
    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getProfile(userId: String): ProfileRemote {
        val response = client.get("$baseUrl/api/v1/users/$userId/profile") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        val body = response.bodyAsText()
        android.util.Log.e("IroZumi", "📋 Perfil response: $body")
        if (response.status.value != 200) throw Exception("Error al cargar perfil")
        return json.decodeFromString(body)
    }

    suspend fun updateProfile(
        userId: String,
        displayName: String?,
        bio: String?,
        instagram: String?,
        twitter: String?,
        profilePictureUrl: String? = null,
        coverPictureUrl: String? = null
    ) {
        // LOG: Qué vamos a enviar
        android.util.Log.e(
            "IroZumi",
            "ENVIANDO updateProfile - userId: $userId, name: $displayName, bio: $bio, insta: $instagram, twitter: $twitter, profileUrl: $profilePictureUrl, coverUrl: $coverPictureUrl"
        )
        val response = client.put("$baseUrl/api/v1/users/$userId/profile") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(
                mapOf(
                    "displayName" to displayName,
                    "bio" to bio,
                    "instagram" to instagram,
                    "twitter" to twitter,
                    "profilePictureUrl" to profilePictureUrl,
                    "coverPictureUrl" to coverPictureUrl
                )
            )
        }
        // LOG: Qué respondió el backend
        android.util.Log.e(
            "IroZumi",
            "RESPUESTA updateProfile - status: ${response.status.value}, body: ${response.bodyAsText()}"
        )
    }

    suspend fun getUserPosts(userId: String, currentUserId: String): List<PostRemote> {
        val response = client.get("$baseUrl/api/v1/users/$userId/posts") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        if (response.status.value != 200) throw Exception("Error al cargar posts")
        return response.body()
    }

    suspend fun toggleLike(postId: String): PostRemote {
        val url = "$baseUrl/api/v1/gallery/posts/$postId/like"  // ← AGREGAR /gallery
        android.util.Log.e("IroZumi", "❤️ URL: $url")
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e(
            "IroZumi",
            "❤️ Like toggle - postId: $postId, status: ${response.status.value}"
        )
        if (response.status.value != 200) throw Exception("Error al dar like")
        return response.body()
    }

    suspend fun uploadProfileImage(base64: String): String {
        val response = client.post("$baseUrl/api/v1/users/upload-image") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf("imageBase64" to base64))
        }
        if (response.status.value != 200) throw Exception("Error al subir imagen")
        val result = json.decodeFromString<Map<String, String>>(response.bodyAsText())
        return result["url"] ?: ""
    }

    suspend fun getComments(postId: String): List<CommentRemote> {
        val response = client.get("$baseUrl/api/v1/gallery/posts/$postId/comments") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e(
            "IroZumi",
            "Cargando comentarios - postId: $postId, status: ${response.status.value}"
        )
        if (response.status.value != 200) throw Exception("Error al cargar comentarios")
        return response.body()
    }

    suspend fun postComment(postId: String, text: String): CommentRemote {
        val response = client.post("$baseUrl/api/v1/gallery/posts/$postId/comments") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf("content" to text))
        }
        android.util.Log.e(
            "IroZumi",
            "Enviando comentario - postId: $postId, status: ${response.status.value}"
        )
        if (response.status.value != 200) throw Exception("Error al enviar comentario")
        return response.body()
    }

    suspend fun toggleFollow(userId: String): Boolean {
        val response = client.post("$baseUrl/api/v1/users/$userId/follow") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e("IroZumi", "Follow toggle - userId: $userId, status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al seguir/dejar de seguir")
        val result: Map<String, Boolean> = response.body()
        return result["isFollowing"] ?: false
    }
}
