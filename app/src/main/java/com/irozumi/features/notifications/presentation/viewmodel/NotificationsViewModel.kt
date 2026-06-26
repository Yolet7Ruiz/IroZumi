package com.irozumi.features.notifications.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.irozumi.features.notifications.domain.model.NotificationItem
import com.irozumi.features.notifications.presentation.screens.NotificationsUiState

class NotificationsViewModel : ViewModel() {

    private val _uiState = mutableStateOf(NotificationsUiState())
    val uiState: State<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        // Datos de prueba limpios mapeados a tus nuevos data classes
        val mockNew = listOf(
            NotificationItem(1, "@BandaRomantica24", "le gustó tu arte", "Hace 23h • Nuevo", true, "Arte"),
            NotificationItem(2, "@BandaRomantica24", "le gustó tu arte", "Hace 23h • Nuevo", true, "Arte")
        )

        val mockRecent = listOf(
            NotificationItem(3, "@BandaRomantica24", "le gustó tu arte", "Hace 23h • Nuevo", false, "Arte")
        )

        _uiState.value = NotificationsUiState(
            newNotifications = mockNew,
            recentNotifications = mockRecent
        )
    }
}