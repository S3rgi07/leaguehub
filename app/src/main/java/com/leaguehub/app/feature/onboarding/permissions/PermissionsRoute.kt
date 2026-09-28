package com.leaguehub.app.feature.onboarding.permissions

import androidx.compose.runtime.Composable

@Composable
fun PermissionsRoute() {
    PermissionsScreen(
        liveMatchesEnabled = true,
        remindersEnabled = true,
        smartPreviewEnabled = true,
        locationEnabled = false
    )
}
