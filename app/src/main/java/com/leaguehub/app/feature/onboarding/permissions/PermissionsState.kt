package com.leaguehub.app.feature.onboarding.permissions

data class PermissionsState(
    val liveMatchesEnabled: Boolean = true,
    val remindersEnabled: Boolean = true,
    val smartPreviewEnabled: Boolean = true,
    val locationEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false
)