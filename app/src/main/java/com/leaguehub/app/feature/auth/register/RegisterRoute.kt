package com.leaguehub.app.feature.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterRoute(
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistrationSuccessful) {
        if (state.isRegistrationSuccessful) {
            onContinue()
        }
    }

    RegisterScreen(
        name = state.name,
        email = state.email,
        password = state.password,
        termsAccepted = state.termsAccepted,
        passwordVisible = state.passwordVisible,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,

        onNameChange = {
            viewModel.onEvent(RegisterEvent.NameChanged(it))
        },

        onEmailChange = {
            viewModel.onEvent(RegisterEvent.EmailChanged(it))
        },

        onPasswordChange = {
            viewModel.onEvent(RegisterEvent.PasswordChanged(it))
        },

        onTermsChange = {
            viewModel.onEvent(RegisterEvent.TermsChanged(it))
        },

        onTogglePasswordVisibility = {
            viewModel.onEvent(RegisterEvent.TogglePasswordVisibility)
        },

        onContinueClick = {
            viewModel.onEvent(RegisterEvent.ContinueClicked)
        },

        onBackClick = onBack
    )
}