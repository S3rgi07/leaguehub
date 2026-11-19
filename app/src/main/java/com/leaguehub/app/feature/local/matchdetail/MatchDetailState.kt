package com.leaguehub.app.feature.local.matchdetail
import com.leaguehub.app.feature.local.*

data class MatchDetailState(val ui:LocalUiState=LocalUiState(),val match:com.leaguehub.app.feature.matches.model.MatchUiModel?=null)
