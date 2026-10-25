package com.leaguehub.app.feature.auth.recovery

sealed interface RecoveryEvent {

    data class EmailChanged(
        val email: String
    ) : RecoveryEvent

    data object SendCodeClicked : RecoveryEvent
}