package com.leaguehub.app.feature.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        email = state.email,
        password = state.password,
        passwordVisible = state.passwordVisible,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onEmailChange = {
            viewModel.onEvent(LoginEvent.EmailChanged(it))
        },
        onPasswordChange = {
            viewModel.onEvent(LoginEvent.PasswordChanged(it))
        },
        onTogglePasswordVisibility = {
            viewModel.onEvent(LoginEvent.TogglePasswordVisibility)
        },
        onLoginClick = {
            viewModel.onEvent(LoginEvent.LoginClicked)
        },
        onForgotPasswordClick = onForgotPasswordClick,
        onCreateAccountClick = onCreateAccountClick
    )
}