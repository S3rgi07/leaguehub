package com.leaguehub.app.feature.local.center
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.data.repository.MatchSheet
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.matches.matchcenter.model.*
data class CourtPlayer(val id:String,val name:String,val number:Int,val onCourt:Boolean)
data class CenterState(
 val ui:LocalUiState=LocalUiState(),val match:MatchUiModel?=null,val league:String="",val tab:MatchCenterTab=MatchCenterTab.SUMMARY,
 val selectedTeam:String="",val lineups:List<TeamLineupUiModel> = emptyList(),val statistics:List<MatchStatisticUiModel> = emptyList(),
 val events:List<EventRow> = emptyList(),val sheet:MatchSheet=MatchSheet(),val players:List<CourtPlayer> = emptyList(),val canManage:Boolean=false,
 val busy:Boolean=false,val error:String?=null,val message:String?=null,val editing:Boolean=false,val formation:String="2-2",val draft:List<String> = emptyList(),
 val outId:String="",val inId:String="",val changeMinute:String="65",val duration:String="90",val clock:String="0",val courtMinute:Int=130,
 val substitutions:List<String> = emptyList()
)
