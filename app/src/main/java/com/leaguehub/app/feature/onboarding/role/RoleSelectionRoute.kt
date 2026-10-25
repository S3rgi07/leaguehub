package com.leaguehub.app.feature.onboarding.role

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun RoleSelectionRoute(
    onContinue: (UserRole) -> Unit = {},
    onBack: () -> Unit = {},
    onConfigureLeague: () -> Unit = {}
) {
    val application = LocalContext.current.applicationContext
            as LeagueHubApplication

    val viewModel: RoleSelectionViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                RoleSelectionViewModel(
                    sessionRepository = application.userSessionRepository
                )
            }
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSelectionConfirmed) {
        if (state.isSelectionConfirmed) {
            onContinue(state.selectedRole)
        }
    }

    RoleSelectionScreen(
        selectedRole = state.selectedRole,

        onRoleSelected = { role ->
            viewModel.onEvent(
                RoleSelectionEvent.RoleSelected(role)
            )
        },

        onContinueClick = {
            viewModel.onEvent(
                RoleSelectionEvent.ContinueClicked
            )
        },

        onBackClick = onBack,
        onConfigureLeagueClick = onConfigureLeague
    )
}