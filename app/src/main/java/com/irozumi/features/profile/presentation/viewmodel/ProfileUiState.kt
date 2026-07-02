package com.irozumi.features.profile.presentation.viewmodel

import android.net.Uri

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isMyProfile: Boolean = true,
    val name: String = "Yolet Ruiz",
    val username: String = "@yolet_art",
    val bio: String = "Artista conceptual | Amante de las acuarelas tradicionales.",
    val profilePictureUri: Uri? = null,
    val coverPictureUri: Uri? = null,
    val instagram: String = "yolet_art_studio",
    val twitter: String = "yolet_draws",
    val isFollowing: Boolean = false,
    val posts: List<ProfilePost> = emptyList(),
    val isEditing: Boolean = false
)

data class ProfilePost(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val commentsCount: Int
)