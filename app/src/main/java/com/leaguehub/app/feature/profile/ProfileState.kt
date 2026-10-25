package com.leaguehub.app.feature.profile

import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

data class ProfileState(
    val player: PlayerUiModel,
    val team: TeamUiModel
)
