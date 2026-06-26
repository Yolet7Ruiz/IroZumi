package com.irozumi.features.challenges.domain.model

import android.net.Uri

data class CustomChallenge(
    val id: String,
    val title: String,
    val description: String,
    val startDate: String,
    val startTime: String,
    val votingDays: Int,
    val referenceImageUri: Uri?
)