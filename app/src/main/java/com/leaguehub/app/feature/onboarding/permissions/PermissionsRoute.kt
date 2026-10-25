package com.leaguehub.app.feature.onboarding.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun PermissionsRoute(
    onFinish: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext
            as LeagueHubApplication

    val viewModel: PermissionsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                PermissionsViewModel(
                    sessionRepository = application.userSessionRepository
                )
            }
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted, state.isSkipped) {
        if (state.isCompleted || state.isSkipped) {
            onFinish()
        }
    }

    PermissionsScreen(
        liveMatchesEnabled = state.liveMatchesEnabled,
        remindersEnabled = state.remindersEnabled,
        smartPreviewEnabled = state.smartPreviewEnabled,
        locationEnabled = state.locationEnabled,

        onLiveMatchesChange = { enabled ->
            viewModel.onEvent(
                PermissionsEvent.LiveMatchesChanged(enabled)
            )
        },

        onRemindersChange = { enabled ->
            viewModel.onEvent(
                PermissionsEvent.RemindersChanged(enabled)
            )
        },

        onSmartPreviewChange = { enabled ->
            viewModel.onEvent(
                PermissionsEvent.SmartPreviewChanged(enabled)
            )
        },

        onLocationChange = { enabled ->
            viewModel.onEvent(
                PermissionsEvent.LocationChanged(enabled)
            )
        },

        onEnterLeagueHubClick = {
            viewModel.onEvent(
                PermissionsEvent.CompleteClicked
            )
        },

        onSkipClick = {
            viewModel.onEvent(
                PermissionsEvent.SkipClicked
            )
        }
    )
}
