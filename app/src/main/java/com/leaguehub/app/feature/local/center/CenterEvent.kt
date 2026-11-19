package com.leaguehub.app.feature.local.center
import android.net.Uri
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab
sealed interface CenterEvent {
 data class Common(val event:LocalEvent):CenterEvent
 data class Tab(val tab:MatchCenterTab):CenterEvent
 data class Team(val id:String):CenterEvent
 data class Action(val action:MatchAction):CenterEvent
 data object EditLineup:CenterEvent
 data object CancelLineup:CenterEvent
 data class Formation(val value:String):CenterEvent
 data class TogglePlayer(val id:String):CenterEvent
 data object SaveLineup:CenterEvent
 data class Field(val key:String,val value:String):CenterEvent
 data object Substitute:CenterEvent
 data object UndoChange:CenterEvent
 data object ApplyMinutes:CenterEvent
 data class CourtMinute(val minute:Int):CenterEvent
 data class Phase(val value:String):CenterEvent
 data object SaveClock:CenterEvent
 data object AddPhoto:CenterEvent
 data class PhotoPicked(val uri:Uri?):CenterEvent
 data class RemovePhoto(val uri:String):CenterEvent
}
