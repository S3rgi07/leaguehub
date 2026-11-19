package com.leaguehub.app.feature.local.dashboard
import com.leaguehub.app.feature.local.*

sealed interface DashboardEvent { data class Common(val event:LocalEvent):DashboardEvent;data class Match(val id:String):DashboardEvent;data object Create:DashboardEvent }
