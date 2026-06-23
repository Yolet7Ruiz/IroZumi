package com.irozumi.features.auth.data.dataSource

import com.irozumi.features.auth.domain.model.UserSession
import kotlinx.coroutines.delay

class AuthRemoteDataSource {
    // 🔮 El martes/miércoles mapearás tu llamada de Retrofit/Ktor aquí adentro
    suspend fun loginWithApi(email: String, password: String): UserSession {
        delay(1000) // Simula la latencia de red real para el indicador de carga
        if (email.isBlank() || password.length < 6) {
            throw Exception("Credenciales inválidas o contraseña muy corta")
        }
        return UserSession("token_mock_123", "ArtistaIroZumi", email)
    }

    suspend fun registerWithApi(username: String, email: String, password: String): UserSession {
        delay(1000)
        if (username.isBlank() || !email.contains("@")) {
            throw Exception("Datos de registro inválidos")
        }
        return UserSession("token_mock_456", username, email)
    }
}