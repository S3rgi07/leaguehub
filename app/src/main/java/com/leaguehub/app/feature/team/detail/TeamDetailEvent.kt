package com.leaguehub.app.feature.team.detail

sealed interface TeamDetailEvent {
    data object FollowToggled : TeamDetailEvent
    data object SquadToggled : TeamDetailEvent
}
