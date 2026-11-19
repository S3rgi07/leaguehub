package com.leaguehub.app.feature.local.leagues
import com.leaguehub.app.feature.local.*
data class LeaguesState(val ui:LocalUiState=LocalUiState(),val leagues:List<LeagueRow> = emptyList())
