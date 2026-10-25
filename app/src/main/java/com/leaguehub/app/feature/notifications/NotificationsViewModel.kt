package com.leaguehub.app.feature.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            is NotificationsEvent.CategoryChanged -> _state.update { it.copy(selectedCategory = event.category) }
            is NotificationsEvent.NotificationClicked -> _state.update { current ->
                current.copy(
                    todayNotifications = current.todayNotifications.markRead(event.notificationId),
                    previousNotifications = current.previousNotifications.markRead(event.notificationId)
                )
            }
            NotificationsEvent.MarkAllReadClicked -> _state.update { current ->
                current.copy(
                    todayNotifications = current.todayNotifications.map { it.copy(isRead = true) },
                    previousNotifications = current.previousNotifications.map { it.copy(isRead = true) }
                )
            }
        }
    }

    private fun List<NotificationUiModel>.markRead(id: String) =
        map { if (it.id == id) it.copy(isRead = true) else it }
}
