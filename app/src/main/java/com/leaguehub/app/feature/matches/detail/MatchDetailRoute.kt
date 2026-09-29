package com.leaguehub.app.feature.matches.detail

import androidx.compose.runtime.Composable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun MatchDetailRoute(matchId: String, onBack: () -> Unit, onOpenMap: (MatchUiModel, Boolean) -> Unit, onShare: (MatchUiModel) -> Unit, onNavigate: (PlayerDestination) -> Unit) {
    val match = (fakePlayerMatches + fakeRecentResults + fakeMatches).firstOrNull { it.id == matchId } ?: upcomingFalconsVsTitans
    MatchDetailScreen(match, onBack, { onOpenMap(match, false) }, { onOpenMap(match, true) }, { onShare(match) }, onNavigate)
}
