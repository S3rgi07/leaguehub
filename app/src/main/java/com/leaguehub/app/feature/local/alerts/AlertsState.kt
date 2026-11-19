package com.leaguehub.app.feature.local.alerts
import com.leaguehub.app.feature.local.*

data class AlertRow(val id:String,val title:String,val body:String,val matchId:String?,val unread:Boolean,val leagueId:String)
data class AlertsState(val ui:LocalUiState=LocalUiState(),val rows:List<AlertRow> = emptyList(),val filter:Int=0,val unread:Int=0,val error:String?=null)
