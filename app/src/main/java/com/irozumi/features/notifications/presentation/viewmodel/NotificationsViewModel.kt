package com.irozumi.features.notifications.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.notifications.data.datasource.NotificationsRemoteDataSource
import com.irozumi.features.notifications.domain.model.NotificationItem
import com.irozumi.features.notifications.presentation.screens.NotificationsUiState
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val dataSource = NotificationsRemoteDataSource()
    private val _uiState = mutableStateOf(NotificationsUiState())
    val uiState: State<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    fun deleteNotification(id: String) {
        android.util.Log.e("IroZumi", "Eliminando notificación: $id")
        viewModelScope.launch {
            try {
                dataSource.deleteNotification(id)
                android.util.Log.e("IroZumi", "Eliminada")
                loadNotifications()
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error: ${e.message}")
            }
            }
    }

    fun muteNotification(id: String) {
        viewModelScope.launch {
            try {
                dataSource.markAsRead(id)
                loadNotifications()
            } catch (e: Exception) { }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val remote = dataSource.getNotifications()
                val new = remote.filter { !it.isRead }.map {
                    NotificationItem(it.id, it.username, it.message, it.createdAt, true, it.type)
                }
                val recent = remote.filter { it.isRead }.map {
                    NotificationItem(it.id, it.username, it.message, it.createdAt, false, it.type)
                }
                _uiState.value = NotificationsUiState(newNotifications = new, recentNotifications = recent)
            } catch (e: Exception) { }
        }
    }
}

