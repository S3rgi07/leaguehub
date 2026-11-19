package com.leaguehub.app.feature.settings

data class SettingsState(
    val userName: String = "Mi perfil",
    val userEmail: String = "",
    val leagueName: String = "Liga Universitaria GT",
    val activeNotifications: Int = 4,
    val appearance: String = "Oscuro",
    val message: String? = null,
    val isLoggedOut: Boolean = false
)
