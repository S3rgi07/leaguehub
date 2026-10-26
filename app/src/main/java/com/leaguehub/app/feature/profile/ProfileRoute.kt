package com.leaguehub.app.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.sofia
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun ProfileRoute(playerId: String = sofia.id, onNavigate: (PlayerDestination) -> Unit, onTeamClick: (TeamUiModel) -> Unit, onSettingsClick: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel(
        key = "profile-$playerId",
        factory = viewModelFactory { initializer { ProfileViewModel(playerId) } }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileScreen(state.player, state.team, { onTeamClick(state.team) }, onSettingsClick, onNavigate)
}
