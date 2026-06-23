package com.irozumi.features.home.presentation.detail.viewmodel

import com.irozumi.features.home.domain.model.Comment

data class PostDetailState(
    val title: String = "",
    val authorName: String = "",
    val description: String = "",
    val isLiked: Boolean = false,
    val likesCount: Int = 0,
    val isFollowing: Boolean = false,
    val comments: List<Comment> = emptyList()
)