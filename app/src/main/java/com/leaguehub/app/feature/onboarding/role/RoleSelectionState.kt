package com.leaguehub.app.feature.onboarding.role

data class RoleSelectionState(
    val selectedRole: UserRole = UserRole.PLAYER,
    val isSelectionConfirmed: Boolean = false
)