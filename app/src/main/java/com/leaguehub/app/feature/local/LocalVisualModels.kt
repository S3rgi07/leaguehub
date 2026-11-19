package com.leaguehub.app.feature.local

import androidx.compose.ui.graphics.Color
import com.leaguehub.app.data.local.*
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.matches.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalState.teamModel(id:String):TeamUiModel {
    val team=data.clubs.find { it.id==id }
    val colors=listOf(Color(0xFF29B6F6),Color(0xFFFF7926),Color(0xFFA783FF),Color(0xFF77BE32))
    val name=team?.name ?: "Equipo no disponible"
    return TeamUiModel(id,name,name.take(3).uppercase(),colors[Math.floorMod(id.hashCode(),colors.size)],squadSize=data.players.count { it.clubId==id })
}
fun LocalState.playerModel(p:PlayerEntity):PlayerUiModel {
    val row=playerRow(p)
    return PlayerUiModel(p.id,p.name,p.number,p.position,p.clubId,row.goals,row.assists,row.played,row.yellow,row.red,row.minutes,row.recentRatings,data.totals(p.id).rating,scoringStreak=row.scoringStreak,goalRank=row.goalRank)
}
fun LocalState.matchModel(f:FixtureEntity)=LocalDateTime.parse(f.kickoff).let { date ->
    val sheet=data.sheets[f.id]
    val live=f.homeScore==null && sheet!=null && sheet.phase!="SCHEDULED"
    fun goals(club:String)=data.events.count { e->e.fixtureId==f.id && e.kind=="GOAL" && data.players.any { it.id==e.playerId && it.clubId==club } }

    MatchUiModel(f.id,teamModel(f.homeId),teamModel(f.awayId),if(live) goals(f.homeId) else f.homeScore,if(live) goals(f.awayId) else f.awayScore,if(live) MatchStatus.LIVE else if(f.homeScore==null) MatchStatus.SCHEDULED else MatchStatus.FINISHED,minute=if(live) "${sheet?.minute}′${if(sheet?.phase=="HALFTIME") " · Descanso" else ""}" else null,matchday=f.matchday,date=date.format(DateTimeFormatter.ofPattern("dd MMM yyyy",java.util.Locale.forLanguageTag("es-MX"))),time=date.format(DateTimeFormatter.ofPattern("HH:mm")),venue=f.venue,localDate=date.toLocalDate())
}

fun com.leaguehub.app.core.components.PlayerDestination.localSection()=when(this) {
 com.leaguehub.app.core.components.PlayerDestination.HOME->LocalSection.HOME
 com.leaguehub.app.core.components.PlayerDestination.STANDINGS->LocalSection.TABLE
 com.leaguehub.app.core.components.PlayerDestination.CALENDAR->LocalSection.MATCHES
 com.leaguehub.app.core.components.PlayerDestination.PROFILE->LocalSection.PROFILE
}
