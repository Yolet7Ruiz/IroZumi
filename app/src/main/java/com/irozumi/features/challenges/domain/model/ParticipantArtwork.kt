package com.irozumi.features.challenges.domain.model

import android.net.Uri

data class ParticipantArtwork(
    val id: String,
    val username: String,
    val title: String,
    val category: String,
    val imageUri: Uri?,
    val votes: Int = 0
)