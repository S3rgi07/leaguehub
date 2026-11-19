package com.leaguehub.app.feature.local.alerts
import com.leaguehub.app.feature.local.*

sealed interface AlertsEvent { data class Common(val event:LocalEvent):AlertsEvent;data class Filter(val index:Int):AlertsEvent;data object MarkAll:AlertsEvent;data class Open(val id:String):AlertsEvent }
