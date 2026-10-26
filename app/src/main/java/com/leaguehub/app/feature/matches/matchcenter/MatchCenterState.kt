package com.leaguehub.app.feature.matches.matchcenter

import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.matchcenter.model.*
import com.leaguehub.app.feature.matches.model.MatchUiModel

data class MatchCenterState(
    val match: MatchUiModel = falconsVsTitans,
    val leagueName: String = universityLeague.name,
    val selectedTab: MatchCenterTab = MatchCenterTab.SUMMARY,
    val events: List<MatchEventUiModel> = fakeMatchEvents,
    val lineups: List<TeamLineupUiModel> = fakeLineups,
    val selectedTeamId: String = falcons.id,
    val statistics: List<MatchStatisticUiModel> = fakeMatchStatistics,
    val momentum: List<Float> = fakeMomentum
)
