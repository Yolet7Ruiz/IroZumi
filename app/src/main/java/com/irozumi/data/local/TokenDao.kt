package com.irozumi.data.local

import androidx.room.*

@Dao
interface TokenDao {
    @Query("SELECT * FROM auth_tokens WHERE id = 1")
    suspend fun getToken(): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: TokenEntity)

    @Query("DELETE FROM auth_tokens")
    suspend fun clearTokens()
}