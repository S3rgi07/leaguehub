package com.leaguehub.app.feature.onboarding.role

sealed interface RoleSelectionEvent {

    data class RoleSelected(
        val role: UserRole
    ) : RoleSelectionEvent

    data object ContinueClicked : RoleSelectionEvent
}