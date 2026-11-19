package com.leaguehub.app.feature.local.events
import com.leaguehub.app.feature.local.*
data class LocalEventsState(val ui:LocalUiState=LocalUiState(),val match:MatchRow?=null,val events:List<EventRow> = emptyList(),val canManage:Boolean=false,val canAdd:Boolean=false)
