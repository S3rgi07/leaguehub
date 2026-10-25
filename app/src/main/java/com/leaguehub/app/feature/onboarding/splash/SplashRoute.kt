package com.leaguehub.app.feature.onboarding.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SplashRoute(
    onFinished: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashEvent.Initialize)
    }

    LaunchedEffect(state.isReady) {
        if (state.isReady) {
            onFinished()
        }
    }

    SplashScreen()
}