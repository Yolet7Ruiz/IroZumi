package com.irozumi.features.home.domain.model

data class Comment(
    val id: String,
    val authorName: String,
    val text: String,
    val timestamp: String
)