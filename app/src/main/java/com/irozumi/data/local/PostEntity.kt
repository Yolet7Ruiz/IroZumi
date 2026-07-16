package com.irozumi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts_cache")
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val imageUrl: String?,
    val category: String,
    val description: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLikedByUser: Boolean
)
