package com.irozumi.features.auth.domain.model

data class OnboardingPage(
    val title: String,
    val description: String,
    val isFinalPage: Boolean = false
)