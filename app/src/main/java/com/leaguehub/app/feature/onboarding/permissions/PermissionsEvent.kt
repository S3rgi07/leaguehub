package com.leaguehub.app.feature.onboarding.permissions

sealed interface PermissionsEvent {

    data class LiveMatchesChanged(
        val enabled: Boolean
    ) : PermissionsEvent

    data class RemindersChanged(
        val enabled: Boolean
    ) : PermissionsEvent

    data class SmartPreviewChanged(
        val enabled: Boolean
    ) : PermissionsEvent

    data class LocationChanged(
        val enabled: Boolean
    ) : PermissionsEvent

    data object CompleteClicked : PermissionsEvent

    data object SkipClicked : PermissionsEvent
}