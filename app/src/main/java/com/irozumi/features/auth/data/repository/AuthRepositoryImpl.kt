package com.irozumi.features.auth.data.repository

import com.irozumi.core.security.TokenManager
import com.irozumi.features.auth.data.dataSource.AuthRemoteDataSource
import com.irozumi.features.auth.domain.model.UserSession
import com.irozumi.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserSession> {
        return try {
            val session = dataSource.loginWithApi(email, password)
            TokenManager.saveTokens(session.token, session.refreshToken)
            TokenManager.saveUserInfo(session.username, session.userId)
            TokenManager.saveRole(session.role)
            android.util.Log.e("IroZumi", "Login - Rol guardado: ${session.role}")
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        artisticLevel: String
    ): Result<UserSession> {
        return try {
            val session = dataSource.registerWithApi(username, email, password, artisticLevel)
            TokenManager.saveTokens(session.token, session.refreshToken)
            TokenManager.saveUserInfo(session.username, session.userId)
            TokenManager.saveRole(session.role)
            android.util.Log.e("IroZumi", "Registro - Rol guardado: ${session.role}")
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}