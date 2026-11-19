package com.leaguehub.app.feature.local.dashboard
import com.leaguehub.app.feature.local.*

data class DashboardState(val ui:LocalUiState=LocalUiState(),val name:String="",val teams:Int=0,val players:Int=0,val played:Int=0,val total:Int=0,val pending:List<MatchRow> = emptyList())
