package com.leaguehub.app.feature.profile

import androidx.compose.runtime.Composable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun ProfileRoute(playerId: String = sofia.id, onNavigate: (PlayerDestination) -> Unit, onTeamClick: (TeamUiModel) -> Unit, onSettingsClick: () -> Unit) {
    val player = fakePlayers.firstOrNull { it.id == playerId } ?: sofia
    val team = fakeTeams.first { it.id == player.teamId }
    ProfileScreen(player, team, { onTeamClick(team) }, onSettingsClick, onNavigate)
}
