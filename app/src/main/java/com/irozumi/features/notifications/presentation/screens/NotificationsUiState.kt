package com.irozumi.features.notifications.presentation.screens

import com.irozumi.features.notifications.domain.model.NotificationItem

// El estado que va a escuchar tu NotificationsScreen
data class NotificationsUiState(
    val newNotifications: List<NotificationItem> = emptyList(),
    val recentNotifications: List<NotificationItem> = emptyList()
)