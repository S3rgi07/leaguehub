package com.leaguehub.app.feature.onboarding.splash

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
fun SplashRoute(
    onFinished: (SplashTarget) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: SplashViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SplashViewModel(application.userSessionRepository,application.database.userAccountDao()) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SplashEvent.Initialize)
    }

    LaunchedEffect(state.isReady) {
        if (state.isReady) {
            state.message?.let { android.widget.Toast.makeText(application,it,android.widget.Toast.LENGTH_LONG).show() }
            state.target?.let(onFinished)
        }
    }

    SplashScreen()
}
