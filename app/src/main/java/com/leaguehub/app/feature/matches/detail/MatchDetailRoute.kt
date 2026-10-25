package com.leaguehub.app.feature.matches.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun MatchDetailRoute(matchId: String, onBack: () -> Unit, onOpenMap: (MatchUiModel, Boolean) -> Unit, onShare: (MatchUiModel) -> Unit, onNavigate: (PlayerDestination) -> Unit) {
    val viewModel: MatchDetailViewModel = viewModel(
        key = "match-$matchId",
        factory = viewModelFactory { initializer { MatchDetailViewModel(matchId) } }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    MatchDetailScreen(state.match, onBack, { onOpenMap(state.match, false) }, { onOpenMap(state.match, true) }, { onShare(state.match) }, onNavigate)
}
