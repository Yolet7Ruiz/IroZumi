package com.irozumi.features.notifications.domain.model

// El modelo puro de lo que es una notificación en tu app
data class NotificationItem(
    val id: String,
    val username: String,
    val actionText: String,
    val timeAgo: String,
    val isNew: Boolean,
    val tag: String
)
