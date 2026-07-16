package com.irozumi.features.profile.presentation.viewmodel

import android.net.Uri

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isMyProfile: Boolean = true,
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val profilePictureUri: Uri? = null,
    val coverPictureUri: Uri? = null,
    val profilePictureUrl: String = "",
    val coverPictureUrl: String = "",
    val instagram: String = "",
    val twitter: String = "",
    val isFollowing: Boolean = false,
    val posts: List<ProfilePost> = emptyList(),
    val isEditing: Boolean = false,
    // NUEVOS CAMPOS PARA COMENTARIOS
    val activePostForComments: ProfilePost? = null,
    val comments: List<CommentData> = emptyList(),
    val isCommentsLoading: Boolean = false,
    val commentError: String? = null,
    val fullScreenImage: String? = null
)

data class ProfilePost(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val commentsCount: Int,
    val imageUrl: String = ""
)

data class CommentData(
    val id: String,
    val authorName: String,
    val text: String
)