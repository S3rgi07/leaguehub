package com.leaguehub.app.feature.local.leaguedetail
import com.leaguehub.app.feature.local.*

data class LeagueDetailState(val ui:LocalUiState=LocalUiState(),val league:com.leaguehub.app.feature.league.model.LeagueUiModel?=null,val teams:List<com.leaguehub.app.feature.team.model.TeamUiModel> = emptyList(),val news:List<com.leaguehub.app.feature.league.model.LeagueNewsUiModel> = emptyList(),val tab:com.leaguehub.app.feature.league.detail.LeagueTab=com.leaguehub.app.feature.league.detail.LeagueTab.OVERVIEW)
