package com.irozumi.features.challenges.domain.model

data class ChallengeWinner(
    val id: Int,
    val rank: Int,
    val username: String,
    val category: String,
    val totalLikes: Int,
    val badgeIcon: String, // Insignia para el perfil (🥇, 🥈, 🥉)
    val avatarUrl: String?
)