package com.leaguehub.app.feature.local.players
import com.leaguehub.app.feature.local.*
data class LocalPlayersState(val ui:LocalUiState=LocalUiState(),val players:List<PlayerRow> = emptyList(),val clubChoices:List<Pair<String,String>> = emptyList(),val selectedClub:String="",val canManage:Boolean=false,val canCreate:Boolean=false)
