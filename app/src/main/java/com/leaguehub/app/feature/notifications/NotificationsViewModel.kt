package com.leaguehub.app.feature.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationsViewModel(private val storage: com.leaguehub.app.data.repository.UserScopedState<NotificationsState>? = null) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = storage?.state ?: _state.asStateFlow()
    private fun change(transform: (NotificationsState) -> NotificationsState) {
        storage?.update(transform) ?: _state.update(transform)
    }

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            is NotificationsEvent.CategoryChanged -> change { it.copy(selectedCategory = event.category) }
            is NotificationsEvent.NotificationClicked -> change { current ->
                current.copy(
                    todayNotifications = current.todayNotifications.markRead(event.notificationId),
                    previousNotifications = current.previousNotifications.markRead(event.notificationId)
                )
            }
            NotificationsEvent.MarkAllReadClicked -> change { current ->
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
