package com.leaguehub.app.feature.local.teamdetail
import com.leaguehub.app.feature.local.*

sealed interface TeamDetailEvent { data class Common(val event:LocalEvent):TeamDetailEvent;data object Follow:TeamDetailEvent;data object Full:TeamDetailEvent;data class Player(val id:String?):TeamDetailEvent;data class Match(val id:String):TeamDetailEvent;data object Share:TeamDetailEvent }
