package com.leaguehub.app.feature.matches.matchcenter

import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab

sealed interface MatchCenterEvent {
    data class TabSelected(val tab: MatchCenterTab) : MatchCenterEvent
    data class TeamSelected(val teamId: String) : MatchCenterEvent
}
