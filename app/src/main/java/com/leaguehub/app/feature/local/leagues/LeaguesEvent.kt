package com.leaguehub.app.feature.local.leagues
import com.leaguehub.app.feature.local.*
sealed interface LeaguesEvent {
    data class Common(val event:LocalEvent):LeaguesEvent
    data class Open(val id:String):LeaguesEvent
    data class Edit(val id:String?=null):LeaguesEvent
    data class Delete(val id:String):LeaguesEvent
}
