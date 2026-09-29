package com.leaguehub.app.feature.league.detail

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.LeagueNewsUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun LeagueDetailRoute(onTeamClick: (TeamUiModel) -> Unit, onNewsClick: (LeagueNewsUiModel) -> Unit, onBack: () -> Unit, onNotificationsClick: () -> Unit, onNavigate: (PlayerDestination) -> Unit) {
    var tab by rememberSaveable { mutableStateOf(LeagueTab.OVERVIEW) }
    val featured = listOf(falcons, wolves, titans)
    val teams = featured + fakeTeams.filter { team -> featured.none { it.id == team.id } }
    LeagueDetailScreen(universityLeague, teams, fakeLeagueNews, tab, { tab = it }, onTeamClick, onNewsClick, onBack, onNotificationsClick, onNavigate)
}
