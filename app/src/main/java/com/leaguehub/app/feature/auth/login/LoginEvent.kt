package com.leaguehub.app.feature.auth.login

sealed interface LoginEvent {

    data class EmailChanged(
        val email: String
    ) : LoginEvent

    data class PasswordChanged(
        val password: String
    ) : LoginEvent

    data object TogglePasswordVisibility : LoginEvent

    data object LoginClicked : LoginEvent
}