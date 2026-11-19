package com.leaguehub.app.feature.local.matches
import com.leaguehub.app.feature.local.*
sealed interface MatchesEvent {
    data class Common(val event:LocalEvent):MatchesEvent
    data class Month(val delta:Long):MatchesEvent
    data class Day(val date:java.time.LocalDate):MatchesEvent
    data object ShowAll:MatchesEvent
    data object Add:MatchesEvent
    data class Action(val id:String,val action:MatchAction):MatchesEvent
}
