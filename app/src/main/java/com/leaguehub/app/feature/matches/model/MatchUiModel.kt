package com.leaguehub.app.feature.matches.model

import com.leaguehub.app.feature.team.model.TeamUiModel

data class MatchUiModel(
    val id: String,
    val homeTeam: TeamUiModel,
    val awayTeam: TeamUiModel,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val status: MatchStatus,
    val minute: String? = null,
    val matchday: Int,
    val date: String,
    val time: String,
    val venue: String
)