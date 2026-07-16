package com.irozumi.features.notifications.presentation.screens

import com.irozumi.features.notifications.domain.model.NotificationItem

/**
 * El estado que va a escuchar tu NotificationsScreen.
 * Incluye infoMessage para mostrar errores o éxitos al usuario.
 */
data class NotificationsUiState(
    val newNotifications: List<NotificationItem> = emptyList(),
    val recentNotifications: List<NotificationItem> = emptyList(),
    val infoMessage: String? = null
)
