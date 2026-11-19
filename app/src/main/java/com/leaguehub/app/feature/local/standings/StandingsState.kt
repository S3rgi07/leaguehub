package com.leaguehub.app.feature.local.standings
import com.leaguehub.app.feature.local.*
data class StandingsState(val ui:LocalUiState=LocalUiState(),val rows:List<PositionRow> = emptyList())
