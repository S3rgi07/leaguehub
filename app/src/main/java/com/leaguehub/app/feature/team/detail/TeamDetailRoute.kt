package com.leaguehub.app.feature.team.detail

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.*
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun TeamDetailRoute(teamId: String, following: Boolean, onFollowToggle: () -> Unit, onPlayerClick: (PlayerUiModel) -> Unit, onMatchClick: (MatchUiModel) -> Unit, onBack: () -> Unit, onShare: (TeamUiModel) -> Unit, onNavigate: (PlayerDestination) -> Unit) {
    val team = fakeTeams.firstOrNull { it.id == teamId } ?: falcons
    var fullSquad by rememberSaveable(teamId) { mutableStateOf(false) }
    val match = fakePlayerMatches.filter { it.status == MatchStatus.SCHEDULED && (it.homeTeam.id == team.id || it.awayTeam.id == team.id) }.minByOrNull { it.localDate ?: java.time.LocalDate.MAX }
    TeamDetailScreen(team, fakeStandings.firstOrNull { it.team.id == team.id }, match,
        fakePlayers.filter { it.teamId == team.id }, following, fullSquad, onFollowToggle, { fullSquad = !fullSquad },
        onPlayerClick, onMatchClick, onBack, { onShare(team) }, onNavigate)
}
