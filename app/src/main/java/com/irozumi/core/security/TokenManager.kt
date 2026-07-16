package com.irozumi.core.security

import com.irozumi.data.local.AppDatabase
import com.irozumi.data.local.TokenEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object TokenManager {
    var accessToken: String = ""
    var refreshToken: String = ""
    var currentUserName: String = ""
    var currentUserId: String = ""
    var currentRole: String = "artist"
        private set
    private var database: AppDatabase? = null
    var isLoaded: Boolean = false

    fun saveRole(role: String) {
        currentRole = role
    }

    fun init(context: android.content.Context) {
        database = AppDatabase.getInstance(context)
        runBlocking {
            val token = database?.tokenDao()?.getToken()
            if (token != null) {
                accessToken = token.accessToken
                refreshToken = token.refreshToken
                currentUserName = token.username
                currentUserId = token.userId
                currentRole = token.role
            }
            isLoaded = true
        }
    }

    fun saveTokens(access: String, refresh: String) {
        accessToken = access
        refreshToken = refresh
        isLoaded = true
        kotlinx.coroutines.MainScope().launch {
            database?.tokenDao()?.saveToken(
                TokenEntity(
                    accessToken = access,
                    refreshToken = refresh,
                    username = currentUserName,
                    userId = currentUserId,
                    role = currentRole
                )
            )
        }
    }

    fun saveUserInfo(name: String, id: String) {
        currentUserName = name
        currentUserId = id
    }

    fun clearTokens() {
        accessToken = ""
        refreshToken = ""
        currentUserName = ""
        currentUserId = ""
        currentRole = "artist"
        isLoaded = false
        kotlinx.coroutines.MainScope().launch {
            database?.tokenDao()?.clearTokens()
        }
    }

    fun getAuthHeader(): String = "Bearer $accessToken"

    fun hasValidToken(): Boolean = isLoaded && accessToken.isNotBlank()
}