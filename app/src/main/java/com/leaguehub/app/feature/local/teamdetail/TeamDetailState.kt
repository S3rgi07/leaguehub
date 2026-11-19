package com.leaguehub.app.feature.local.teamdetail
import com.leaguehub.app.feature.local.*

data class TeamDetailState(val ui:LocalUiState=LocalUiState(),val team:com.leaguehub.app.feature.team.model.TeamUiModel?=null,val standing:com.leaguehub.app.feature.league.model.StandingUiModel?=null,val next:com.leaguehub.app.feature.matches.model.MatchUiModel?=null,val players:List<com.leaguehub.app.feature.profile.model.PlayerUiModel> = emptyList(),val following:Boolean=false,val full:Boolean=false,val selected:com.leaguehub.app.feature.profile.model.PlayerUiModel?=null)
