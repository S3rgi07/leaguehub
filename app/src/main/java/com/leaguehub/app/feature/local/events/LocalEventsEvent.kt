package com.leaguehub.app.feature.local.events
import com.leaguehub.app.feature.local.*
sealed interface LocalEventsEvent {
 data class Common(val event:LocalEvent):LocalEventsEvent
 data object Add:LocalEventsEvent
 data class Edit(val id:String):LocalEventsEvent
 data class Delete(val id:String):LocalEventsEvent
 data class Action(val action:MatchAction):LocalEventsEvent
}
