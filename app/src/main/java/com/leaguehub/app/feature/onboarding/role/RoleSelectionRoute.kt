package com.leaguehub.app.feature.onboarding.role

import androidx.compose.runtime.Composable

@Composable
fun RoleSelectionRoute() {
    RoleSelectionScreen(
        selectedRole = UserRole.PLAYER
    )
}