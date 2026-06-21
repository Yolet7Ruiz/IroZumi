package com.example.irozumi.features.auth.data.repository

import com.example.irozumi.features.auth.data.dataSource.AuthRemoteDataSource
import com.example.irozumi.features.auth.domain.model.UserSession
import com.example.irozumi.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserSession> {
        return try {
            val session = dataSource.loginWithApi(email, password)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<UserSession> {
        return try {
            val session = dataSource.registerWithApi(username, email, password)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}