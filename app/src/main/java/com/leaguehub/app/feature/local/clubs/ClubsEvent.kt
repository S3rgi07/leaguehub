package com.leaguehub.app.feature.local.clubs
import com.leaguehub.app.feature.local.*
sealed interface ClubsEvent {
    data class Common(val event:LocalEvent):ClubsEvent
    data object Add:ClubsEvent
    data class Edit(val id:String):ClubsEvent
    data class Delete(val id:String):ClubsEvent
    data class Follow(val id:String):ClubsEvent
    data class Players(val id:String):ClubsEvent
}
