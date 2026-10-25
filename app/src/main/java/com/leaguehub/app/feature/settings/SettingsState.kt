package com.leaguehub.app.feature.settings

data class SettingsState(
    val userName: String = "Sofía Méndez",
    val userEmail: String = "sofia@leaguehub.app",
    val leagueName: String = "Liga Universitaria GT",
    val activeNotifications: Int = 4,
    val appearance: String = "Oscuro",
    val message: String? = null,
    val isLoggedOut: Boolean = false
)
