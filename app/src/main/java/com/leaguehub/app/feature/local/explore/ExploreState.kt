package com.leaguehub.app.feature.local.explore
import com.leaguehub.app.feature.local.*

data class ExploreState(val ui:LocalUiState=LocalUiState(),val query:String="",val leagues:List<LeagueRow> = emptyList())
