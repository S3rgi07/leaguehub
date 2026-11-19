package com.leaguehub.app.feature.local.matchdetail
import com.leaguehub.app.feature.local.*

sealed interface MatchDetailEvent { data class Common(val event:LocalEvent):MatchDetailEvent;data object Map:MatchDetailEvent;data object Share:MatchDetailEvent;data object Center:MatchDetailEvent }
