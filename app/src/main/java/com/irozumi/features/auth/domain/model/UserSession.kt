package com.irozumi.features.auth.domain.model

data class UserSession(
    val token: String,
    val refreshToken: String = "",
    val username: String,
    val email: String,
    val userId: String = "",
    val role: String = "artist"
)