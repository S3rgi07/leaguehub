package com.leaguehub.app.feature.league.detail

import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.LeagueNewsUiModel
import com.leaguehub.app.feature.league.model.LeagueUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

data class LeagueDetailState(
    val league: LeagueUiModel = universityLeague,
    val teams: List<TeamUiModel> = listOf(falcons, wolves, titans) +
        fakeTeams.filter { it.id !in setOf(falcons.id, wolves.id, titans.id) },
    val news: List<LeagueNewsUiModel> = fakeLeagueNews,
    val selectedTab: LeagueTab = LeagueTab.OVERVIEW,
    val selectedNews: LeagueNewsUiModel? = null
)
