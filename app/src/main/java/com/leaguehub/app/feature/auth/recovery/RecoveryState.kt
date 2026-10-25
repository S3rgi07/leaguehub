package com.leaguehub.app.feature.auth.recovery

data class RecoveryState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val codeSent: Boolean = false
)