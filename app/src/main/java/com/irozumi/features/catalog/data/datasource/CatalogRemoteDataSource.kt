package com.irozumi.features.catalog.data.datasource

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
import io.ktor.client.request.setBody

@Serializable
data class CatalogRemote(
    val id: String, val title: String, val price: Double,
    val category: String, val imageUrl: String?, val rating: Double,
    val artistName: String, val artistId: String = "", val createdAt: String
)

@Serializable
data class CreateCatalogRequest(
    val title: String,
    val price: Double,
    val category: String,
    val imageUrl: String
)

class CatalogRemoteDataSource {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; isLenient = true }) }
    }
    private val baseUrl = Config.BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCatalog(category: String? = null): List<CatalogRemote> {
        val url = if (category != null && category != "Todos") "$baseUrl/api/v1/catalog?category=$category" else "$baseUrl/api/v1/catalog"
        val response = client.get(url) { header("Authorization", TokenManager.getAuthHeader()) }
        if (response.status.value != 200) throw Exception("Error al cargar catálogo")
        return json.decodeFromString(response.bodyAsText())
    }

    suspend fun createProduct(title: String, price: Double, category: String, imageBase64: String) {
        android.util.Log.e("IroZumi", "Enviando al backend...")
        val response = client.post("$baseUrl/api/v1/catalog") {
            contentType(ContentType.Application.Json)
            header("Authorization", TokenManager.getAuthHeader())
            setBody(CreateCatalogRequest(title, price, category, imageBase64))
        }
        android.util.Log.e("IroZumi", "Respuesta: ${response.status.value}")
        if (response.status.value != 201) throw Exception("Error al publicar")
    }
}
