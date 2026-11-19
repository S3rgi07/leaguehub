package com.leaguehub.app.feature.local.leaguedetail
import com.leaguehub.app.feature.local.*

sealed interface LeagueDetailEvent { data class Common(val event:LocalEvent):LeagueDetailEvent;data class Tab(val tab:com.leaguehub.app.feature.league.detail.LeagueTab):LeagueDetailEvent;data class Team(val id:String):LeagueDetailEvent;data class News(val id:String):LeagueDetailEvent }
