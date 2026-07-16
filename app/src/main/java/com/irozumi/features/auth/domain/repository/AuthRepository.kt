package com.irozumi.features.auth.domain.repository

import com.irozumi.features.auth.domain.model.UserSession

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun register(username: String, email: String, password: String, artisticLevel: String): Result<UserSession>
}