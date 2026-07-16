package com.irozumi.features.home.data.repository

import com.irozumi.data.local.PostDao
import com.irozumi.data.local.PostEntity
import com.irozumi.features.home.data.dataSource.HomeRemoteDataSource
import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.domain.model.Comment
import com.irozumi.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val remoteDataSource: HomeRemoteDataSource,
    private val postDao: PostDao
) : HomeRepository {

    override suspend fun getPosts(category: String, query: String): List<ArtworkPost> {
        return try {
            val remotePosts = remoteDataSource.getPosts(category, query)
            postDao.clearAllPosts()
            postDao.insertPosts(remotePosts.map { it.toEntity() })
            remotePosts
        } catch (e: Exception) {
            postDao.getAllPosts().map { it.toDomain() }
        }
    }

    override suspend fun toggleLike(postId: String): Result<Unit> {
        return try {
            val response = remoteDataSource.toggleLike(postId)
            postDao.updateLike(postId, response.likesCount > 0, response.likesCount)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun getComments(postId: String): Result<List<Comment>> {
        return try {
            val comments = remoteDataSource.getComments(postId).map { response ->
                Comment(
                    id = response.id,
                    authorName = response.author.username,
                    text = response.content,
                    timestamp = response.createdAt
                )
            }
            Result.success(comments)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun postComment(postId: String, content: String): Result<Comment> {
        return try {
            val response = remoteDataSource.postComment(postId, content)
            Result.success(Comment(response.id, response.author.username, response.content, response.createdAt))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun createPost(title: String, description: String, category: String, imageBase64: String): Result<Unit> {
        return try {
            remoteDataSource.createPost(title, description, category, imageBase64)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            remoteDataSource.deletePost(postId)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun PostEntity.toDomain() = ArtworkPost(
        id = id, title = title, author = author, imageUrl = imageUrl,
        category = category, description = description, likesCount = likesCount,
        comments = commentsCount, shares = sharesCount, isLikedByUser = isLikedByUser
    )

    private fun ArtworkPost.toEntity() = PostEntity(
        id = id, title = title, author = author, imageUrl = imageUrl,
        category = category, description = description, likesCount = likesCount,
        commentsCount = comments, sharesCount = shares, isLikedByUser = isLikedByUser
    )
}
