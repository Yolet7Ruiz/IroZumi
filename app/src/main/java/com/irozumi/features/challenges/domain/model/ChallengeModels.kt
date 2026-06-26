package com.irozumi.features.challenges.domain.model

enum class ChallengeStage {
    UPCOMING,      // Próximo
    ACTIVE_UPLOAD, // En vivo (Fase de participación)
    VOTING,        // Fase de Votación (Por 3 días)
    CLOSED         // Cerrado (Resultados)
}

data class ChallengeDynamic(
    val id: Int,
    val title: String,
    val iconEmoji: String,
    val theme: String,
    val description: String,
    val referenceImageUrl: String?,
    val stage: ChallengeStage,
    val timingStatusText: String,
    val participantCount: Int,
    val hasUserActivatedNotification: Boolean = false,
    val isUserParticipating: Boolean = false
)

data class GalleryParticipant(
    val id: Int,
    val username: String,
    val category: String,
    val artworkUrl: String
)