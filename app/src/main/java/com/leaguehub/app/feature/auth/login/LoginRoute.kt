
package com.leaguehub.app.feature.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext
            as LeagueHubApplication

    val viewModel: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                LoginViewModel(
                    sessionRepository = application.userSessionRepository
                )
            }
        }
    )

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

        onEmailChange = { email ->
            viewModel.onEvent(
                LoginEvent.EmailChanged(email)
            )
        },

        onPasswordChange = { password ->
            viewModel.onEvent(
                LoginEvent.PasswordChanged(password)
            )
        },

        onTogglePasswordVisibility = {
            viewModel.onEvent(
                LoginEvent.TogglePasswordVisibility
            )
        },

        onLoginClick = {
            viewModel.onEvent(
                LoginEvent.LoginClicked
            )
        },

        onForgotPasswordClick = onForgotPasswordClick,
        onCreateAccountClick = onCreateAccountClick
    )
}
