package com.irozumi.features.home.domain.repository

import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.domain.model.Comment

interface HomeRepository {
    suspend fun getPosts(category: String, query: String): List<ArtworkPost>
    suspend fun toggleLike(postId: String): Result<Unit>
    suspend fun getComments(postId: String): Result<List<Comment>>
    suspend fun postComment(postId: String, content: String): Result<Comment>
    suspend fun createPost(title: String, description: String, category: String, imageBase64: String): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
}