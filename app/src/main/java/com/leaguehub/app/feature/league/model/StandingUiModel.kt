package com.leaguehub.app.feature.league.model

import com.leaguehub.app.feature.team.model.TeamUiModel

data class StandingUiModel(
    val position: Int,
    val team: TeamUiModel,
    val played: Int,
    val goalDifference: Int,
    val points: Int
)