package com.leaguehub.app.feature.local.profile
import com.leaguehub.app.feature.local.*
data class LocalProfileState(val ui:LocalUiState=LocalUiState(),val player:PlayerRow?=null,val followed:List<ClubRow> = emptyList())
