package com.leaguehub.app.feature.local.clubs
import com.leaguehub.app.feature.local.*
data class ClubsState(val ui:LocalUiState=LocalUiState(),val clubs:List<ClubRow> = emptyList(),val canManage:Boolean=false)
