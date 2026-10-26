package com.leaguehub.app.feature.organizer.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun LiveControlRoute(
    matchId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onFinished: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: LiveControlViewModel = viewModel(
        key = "live-$matchId",
        factory = viewModelFactory {
            initializer { LiveControlViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished(matchId)
    }
    LiveControlScreen(
        homeScore = state.homeScore,
        awayScore = state.awayScore,
        matchTime = state.matchTime,
        recentEvents = state.recentEvents,
        onBackClick = onBack,
        onShareClick = onShare,
        onGoalClick = { viewModel.onEvent(LiveControlEvent.GoalClicked) },
        onCardClick = { viewModel.onEvent(LiveControlEvent.CardClicked) },
        onSubstitutionClick = { viewModel.onEvent(LiveControlEvent.SubstitutionClicked) },
        onPauseClick = { viewModel.onEvent(LiveControlEvent.PauseClicked) },
        onEventEditClick = { viewModel.onEvent(LiveControlEvent.EventEditClicked(it)) },
        onFinishMatchClick = { viewModel.onEvent(LiveControlEvent.FinishMatchClicked) }
    )
}

