package com.leaguehub.app.feature.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NotificationsRoute(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotificationsScreen(
        todayNotifications = state.visibleTodayNotifications,
        previousNotifications = state.visiblePreviousNotifications,
        selectedCategory = state.selectedCategory,
        unreadCount = state.unreadCount,
        onCategoryChange = { viewModel.onEvent(NotificationsEvent.CategoryChanged(it)) },
        onNotificationClick = { viewModel.onEvent(NotificationsEvent.NotificationClicked(it)) },
        onMarkAllReadClick = { viewModel.onEvent(NotificationsEvent.MarkAllReadClicked) },
        onBackClick = onBack
    )
}

