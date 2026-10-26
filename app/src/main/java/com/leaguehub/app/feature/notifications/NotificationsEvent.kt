package com.leaguehub.app.feature.notifications

sealed interface NotificationsEvent {
    data class CategoryChanged(val category: NotificationCategory) : NotificationsEvent
    data class NotificationClicked(val notificationId: String) : NotificationsEvent
    data object MarkAllReadClicked : NotificationsEvent
}
