package com.leaguehub.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun HomeRoute(
    onNavigate: (PlayerDestination) -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onLeagueClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HomeViewModel(application.communityRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state.player, state.league, state.nextMatch, state.recentResults, state.prediction,
        onNavigate, onMatchClick, onLeagueClick, onNotificationsClick,
        communityAnnouncement = state.communityAnnouncement,
        announcementError = state.announcementError,
        onRetryAnnouncement = {
            viewModel.onEvent(HomeEvent.RetryAnnouncementClicked)
        }
    )
}
