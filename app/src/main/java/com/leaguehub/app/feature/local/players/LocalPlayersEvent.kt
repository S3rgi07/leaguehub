package com.leaguehub.app.feature.local.players
import com.leaguehub.app.feature.local.*
sealed interface LocalPlayersEvent {
 data class Common(val event:LocalEvent):LocalPlayersEvent
 data class Filter(val clubId:String):LocalPlayersEvent
 data object Add:LocalPlayersEvent
 data class Edit(val id:String):LocalPlayersEvent
 data class Delete(val id:String):LocalPlayersEvent
}
