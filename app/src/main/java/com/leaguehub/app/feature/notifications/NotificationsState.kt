package com.leaguehub.app.feature.notifications

data class NotificationsState(
    val todayNotifications: List<NotificationUiModel> = previewToday,
    val previousNotifications: List<NotificationUiModel> = previewPrevious,
    val selectedCategory: NotificationCategory = NotificationCategory.ALL
) {
    val unreadCount: Int
        get() = (todayNotifications + previousNotifications).count { !it.isRead }

    val visibleTodayNotifications: List<NotificationUiModel>
        get() = todayNotifications.filterBy(selectedCategory)

    val visiblePreviousNotifications: List<NotificationUiModel>
        get() = previousNotifications.filterBy(selectedCategory)
}

private fun List<NotificationUiModel>.filterBy(category: NotificationCategory) = when (category) {
    NotificationCategory.ALL -> this
    NotificationCategory.MATCHES -> filter {
        it.kind == NotificationKind.GOAL ||
            it.kind == NotificationKind.REMINDER ||
            it.kind == NotificationKind.PREDICTION
    }
    NotificationCategory.LEAGUE -> filter {
        it.kind == NotificationKind.STANDINGS ||
            it.kind == NotificationKind.CALLUP ||
            it.kind == NotificationKind.GALLERY
    }
}
