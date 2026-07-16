package com.irozumi.features.gym.data.datasource

import com.irozumi.core.config.Config
import com.irozumi.core.security.TokenManager
import com.irozumi.features.gym.domain.model.GymExercise
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.irozumi.features.gym.domain.model.AntiBlockTip
@Serializable
data class GymExerciseRemote(
    val id: String,
    val title: String,
    val description: String? = null,
    val category: String = "General",
    val difficulty: String = "Intermedio",
    val durationMinutes: Int = 15,
    val pointsReward: Int = 10,
    val imageUrl: String? = null,
    val createdBy: String? = null,
    val createdAt: String? = null
)

@Serializable
data class TipRemote(
    val id: String,
    val title: String,
    val description: String,
    val category: String = "Creatividad",
    val authorName: String = "Anónimo"
)

@Serializable
data class CreateExerciseRequest(
    val title: String,
    val description: String? = null,
    val category: String = "General",
    val difficulty: String = "Intermedio",
    val durationMinutes: Int = 15,
    val pointsReward: Int = 10,
    val imageBase64: String? = null
)

class GymRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }
    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getExercises(): List<GymExercise> {
        val response = client.get("$baseUrl/api/v1/gym/exercises") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e("IroZumi", "Gym exercises - status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al cargar ejercicios")
        val remotes: List<GymExerciseRemote> = response.body()
        return remotes.map { it.toDomain() }
    }

    suspend fun createExercise(
        title: String,
        description: String?,
        category: String,
        difficulty: String,
        durationMinutes: Int,
        pointsReward: Int,
        imageBase64: String?
    ): GymExercise {
        val response = client.post("$baseUrl/api/v1/gym/exercises") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(CreateExerciseRequest(
                title = title,
                description = description,
                category = category,
                difficulty = difficulty,
                durationMinutes = durationMinutes,
                pointsReward = pointsReward,
                imageBase64 = imageBase64
            ))
        }
        android.util.Log.e("IroZumi", "Crear ejercicio - status: ${response.status.value}")
        if (response.status.value !in 200..201) throw Exception("Error al crear ejercicio")
        val remote: GymExerciseRemote = response.body()
        return remote.toDomain()
    }

    suspend fun submitPractice(exerciseId: String, imageBase64: String, notes: String?) {
        val response = client.post("$baseUrl/api/v1/gym/submissions") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf(
                "exerciseId" to exerciseId,
                "imageBase64" to imageBase64,
                "notes" to notes
            ))
        }
        android.util.Log.e("IroZumi", "Submit práctica - status: ${response.status.value}")
        if (response.status.value !in 200..201) throw Exception("Error al subir práctica")
    }

    suspend fun deleteExercise(exerciseId: String) {
        val response = client.delete("$baseUrl/api/v1/gym/exercises/$exerciseId") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e("IroZumi", "Eliminar ejercicio - status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al eliminar ejercicio")
    }

    suspend fun getTips(): List<AntiBlockTip> {
        val response = client.get("$baseUrl/api/v1/gym/tips") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e("IroZumi", "Cargando tips - status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al cargar tips")
        val remotes: List<TipRemote> = response.body()
        return remotes.map { AntiBlockTip(id = it.id.hashCode(), title = it.title, description = it.description, category = it.category, author = it.authorName) }    }

    suspend fun createTip(title: String, description: String, category: String) {
        val response = client.post("$baseUrl/api/v1/gym/tips") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(mapOf(
                "title" to title,
                "description" to description,
                "category" to category,
                "authorName" to TokenManager.currentUserName
            ))
        }
        android.util.Log.e("IroZumi", "Crear tip - status: ${response.status.value}")
        if (response.status.value !in 200..201) throw Exception("Error al crear tip")
    }


    suspend fun updateExercise(
        exerciseId: String,
        title: String,
        description: String?,
        category: String,
        difficulty: String,
        durationMinutes: Int,
        pointsReward: Int,
        imageBase64: String?
    ): GymExercise {
        val response = client.put("$baseUrl/api/v1/gym/exercises/$exerciseId") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(CreateExerciseRequest(
                title = title,
                description = description,
                category = category,
                difficulty = difficulty,
                durationMinutes = durationMinutes,
                pointsReward = pointsReward,
                imageBase64 = imageBase64
            ))
        }
        android.util.Log.e("IroZumi", "Actualizar ejercicio - status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al actualizar ejercicio")
        val remote: GymExerciseRemote = response.body()
        return remote.toDomain()
    }

    suspend fun getMyStreak(): GymStreakRemote {
        val response = client.get("$baseUrl/api/v1/gym/my-streak") {
            header("Authorization", TokenManager.getAuthHeader())
        }
        android.util.Log.e("IroZumi", "Mi racha - status: ${response.status.value}")
        if (response.status.value != 200) throw Exception("Error al cargar racha")
        return response.body()
    }

    @Serializable
    data class GymStreakRemote(
        val currentStreakDays: Int,
        val longestStreak: Int,
        val totalPractices: Int,
        val totalPoints: Int,
        val lastPracticeDate: String?
    )

    private fun GymExerciseRemote.toDomain() = GymExercise(
        id = id,
        title = title,
        description = description,
        category = category,
        difficulty = difficulty,
        durationMinutes = durationMinutes,
        pointsReward = pointsReward,
        imageUrl = imageUrl,
        createdBy = createdBy,
        createdAt = createdAt
    )
}