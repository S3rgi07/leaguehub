package com.leaguehub.app.feature.auth.recovery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecoveryRoute(
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {},
    viewModel: RecoveryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecoveryScreen(
        email = state.email,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        codeSent = state.codeSent,

        onEmailChange = { email ->
            viewModel.onEvent(
                RecoveryEvent.EmailChanged(email)
            )
        },

        onSendCodeClick = {
            viewModel.onEvent(
                RecoveryEvent.SendCodeClicked
            )
        },

        onBackClick = onBack,
        onLoginClick = onLogin
    )
}
