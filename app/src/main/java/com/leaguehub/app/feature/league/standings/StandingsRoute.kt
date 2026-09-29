package com.leaguehub.app.feature.league.standings

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun StandingsRoute(onNavigate: (PlayerDestination) -> Unit, onTeamClick: (TeamUiModel) -> Unit, onLeagueClick: () -> Unit) {
    var scope by rememberSaveable { mutableStateOf(StandingsScope.GENERAL) }
    val rows = when (scope) {
        StandingsScope.GENERAL -> fakeStandings
        StandingsScope.HOME -> fakeHomeStandings
        StandingsScope.AWAY -> fakeAwayStandings
    }
    StandingsScreen(universityLeague, rows, falcons.id, scope, "Actualizada hace 2 min", { scope = it }, onTeamClick, onLeagueClick, onNavigate)
}
