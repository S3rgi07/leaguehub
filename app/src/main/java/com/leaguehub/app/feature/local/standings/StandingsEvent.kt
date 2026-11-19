package com.leaguehub.app.feature.local.standings
import com.leaguehub.app.feature.local.*
sealed interface StandingsEvent {
    data class Common(val event:LocalEvent):StandingsEvent
    data class Filter(val index:Int):StandingsEvent
}
