package com.leaguehub.app.feature.home

import androidx.compose.runtime.Composable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun HomeRoute(onNavigate: (PlayerDestination) -> Unit, onMatchClick: (MatchUiModel) -> Unit, onLeagueClick: () -> Unit, onNotificationsClick: () -> Unit) {
    HomeScreen(sofia, universityLeague, upcomingFalconsVsTitans, fakeRecentResults, fakePrediction, onNavigate, onMatchClick, onLeagueClick, onNotificationsClick)
}
