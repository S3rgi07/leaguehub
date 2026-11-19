package com.leaguehub.app.feature.local.matches
import com.leaguehub.app.feature.local.*
data class MatchesState(val ui:LocalUiState=LocalUiState(),val matches:List<MatchRow> = emptyList(),val canManage:Boolean=false,val canCreate:Boolean=false,val needTeams:Boolean=false)
