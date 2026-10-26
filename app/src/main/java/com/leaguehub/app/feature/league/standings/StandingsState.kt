package com.leaguehub.app.feature.league.standings

import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.LeagueUiModel
import com.leaguehub.app.feature.league.model.StandingUiModel

data class StandingsState(
    val league: LeagueUiModel = universityLeague,
    val standings: List<StandingUiModel> = fakeStandings,
    val selectedTeamId: String? = falcons.id,
    val scope: StandingsScope = StandingsScope.GENERAL,
    val updatedLabel: String = "Actualizada hace 2 min"
)
