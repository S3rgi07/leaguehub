package com.leaguehub.app.feature.local.home
import com.leaguehub.app.feature.local.*
sealed interface LocalHomeEvent {
 data class Common(val event:LocalEvent):LocalHomeEvent
 data class MatchActionSelected(val id:String,val action:MatchAction):LocalHomeEvent
 data object RetryAnnouncement:LocalHomeEvent
}
