package com.example.irozumi.features.auth.data.dataSource

import com.example.irozumi.features.auth.domain.model.UserSession

class AuthRemoteDataSource {
    // 🔮 Aquí mapearás tu llamada de Retrofit/Ktor en el día 5
    suspend fun loginWithApi(email: String, password: String): UserSession {
        return UserSession("token_mock_123", "ArtistaIroZumi", email)
    }

    suspend fun registerWithApi(username: String, email: String, password: String): UserSession {
        return UserSession("token_mock_456", username, email)
    }
}