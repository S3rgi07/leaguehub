package com.leaguehub.app.feature.auth.register

sealed interface RegisterEvent {

    data class NameChanged(
        val name: String
    ) : RegisterEvent

    data class EmailChanged(
        val email: String
    ) : RegisterEvent

    data class PasswordChanged(
        val password: String
    ) : RegisterEvent

    data class TermsChanged(
        val accepted: Boolean
    ) : RegisterEvent

    data object TogglePasswordVisibility : RegisterEvent

    data object ContinueClicked : RegisterEvent
}