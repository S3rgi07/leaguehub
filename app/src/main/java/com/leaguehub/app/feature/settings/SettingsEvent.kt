package com.leaguehub.app.feature.settings

sealed interface SettingsEvent {
    data object AppearanceClicked : SettingsEvent
    data object PrivacyClicked : SettingsEvent
    data object LogoutClicked : SettingsEvent
    data object MessageDismissed : SettingsEvent
}
