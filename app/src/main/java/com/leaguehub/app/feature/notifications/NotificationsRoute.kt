package com.leaguehub.app.feature.notifications

import androidx.compose.runtime.Composable

@Composable
fun NotificationsRoute() {
    NotificationsScreen(
        todayNotifications = previewToday,
        previousNotifications = previewPrevious
    )
}

