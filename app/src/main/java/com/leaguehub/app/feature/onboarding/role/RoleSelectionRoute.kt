package com.leaguehub.app.feature.onboarding.role

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RoleSelectionRoute(
    onContinue: (UserRole) -> Unit = {},
    onBack: () -> Unit = {},
    onConfigureLeague: () -> Unit = {},
    viewModel: RoleSelectionViewModel = viewModel()
) {
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