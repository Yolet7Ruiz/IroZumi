package com.irozumi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Query("SELECT * FROM posts_cache")
    suspend fun getAllPosts(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts_cache")
    suspend fun clearAllPosts()

    @Query("UPDATE posts_cache SET isLikedByUser = :isLiked, likesCount = :newCount WHERE id = :postId")
    suspend fun updateLike(postId: String, isLiked: Boolean, newCount: Int)
}
