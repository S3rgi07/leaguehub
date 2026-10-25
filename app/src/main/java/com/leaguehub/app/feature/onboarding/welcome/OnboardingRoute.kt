package com.leaguehub.app.feature.onboarding.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingRoute(
    onStart: () -> Unit = {},
    onLogin: () -> Unit = {},
    viewModel: OnboardingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigationAction) {
        when (state.navigationAction) {
            OnboardingAction.START -> {
                viewModel.onEvent(OnboardingEvent.NavigationHandled)
                onStart()
            }

            OnboardingAction.LOGIN -> {
                viewModel.onEvent(OnboardingEvent.NavigationHandled)
                onLogin()
            }

            OnboardingAction.NONE -> Unit
        }
    }

    OnboardingScreen(
        onStartClick = {
            viewModel.onEvent(OnboardingEvent.StartClicked)
        },
        onLoginClick = {
            viewModel.onEvent(OnboardingEvent.LoginClicked)
        }
    )
}