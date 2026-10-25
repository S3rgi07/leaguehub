package com.leaguehub.app.feature.team.detail

import com.leaguehub.app.feature.league.model.StandingUiModel
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

data class TeamDetailState(
    val team: TeamUiModel,
    val standing: StandingUiModel?,
    val nextMatch: MatchUiModel?,
    val players: List<PlayerUiModel>,
    val isFollowing: Boolean,
    val showFullSquad: Boolean = false
)
