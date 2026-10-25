package com.leaguehub.app.feature.team.detail

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
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

@Composable
fun TeamDetailRoute(
    teamId: String,
    onPlayerClick: (PlayerUiModel) -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onBack: () -> Unit,
    onShare: (TeamUiModel) -> Unit,
    onNavigate: (PlayerDestination) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: TeamDetailViewModel = viewModel(
        key = "team-$teamId",
        factory = viewModelFactory {
            initializer { TeamDetailViewModel(teamId, application.followedTeamsRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    TeamDetailScreen(
        state.team, state.standing, state.nextMatch, state.players, state.isFollowing, state.showFullSquad,
        { viewModel.onEvent(TeamDetailEvent.FollowToggled) },
        { viewModel.onEvent(TeamDetailEvent.SquadToggled) },
        onPlayerClick, onMatchClick, onBack, { onShare(state.team) }, onNavigate
    )
}
