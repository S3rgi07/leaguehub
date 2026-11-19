package com.leaguehub.app.feature.local
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.session.SessionRole
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LocalUiState(val name:String="",val email:String="",val roleLabel:String="",val organizer:Boolean=false,
    val leagueId:String?=null,val leagueChoices:List<Pair<String,String>> = emptyList(),val loading:Boolean=true,val busy:Boolean=false,
    val error:String?=null,val message:String?=null,val editor:LocalEditor?=null,val deletion:PendingDelete?=null,val logoutConfirmation:Boolean=false,val appearance:String="Oscuro")
fun LocalState.ui()=LocalUiState(session.userName ?: "Mi perfil",session.email.orEmpty(),if(session.role==SessionRole.ORGANIZER) "Organizador" else "Jugador / aficionado",session.role==SessionRole.ORGANIZER,
    leagueId,data.leagues.map { it.id to "${it.name} · ${it.season}" },loading,busy,error,message,editor,deletion,logoutConfirmation,appearance)
data class LeagueRow(val id:String,val name:String,val season:String,val selected:Boolean,val canEdit:Boolean)
data class ClubRow(val id:String,val leagueId:String,val name:String,val playerCount:Int,val following:Boolean)
data class PlayerRow(val id:String,val name:String,val number:Int,val team:String,val position:String,val stats:String,val discipline:String,val rating:String?,val isMe:Boolean,val goals:Int=0,val assists:Int=0,val played:Int=0,val yellow:Int=0,val red:Int=0,val minutes:Int=0,val recentRatings:List<Double> = emptyList(),val scoringStreak:Int=0,val goalRank:Int?=null)
data class MatchRow(val id:String,val title:String,val score:String,val scheduled:Boolean,val date:String,val venue:String,val shareText:String,val home:String="",val away:String="",val scoreline:String="VS",val day:java.time.LocalDate?=null)
data class EventRow(val id:String,val title:String,val player:String,val details:String?)
data class PositionRow(val id:String,val title:String,val numbers:String,val name:String="",val position:Int=0,val played:Int=0,val difference:Int=0,val points:Int=0)
fun LocalState.playerRow(player:PlayerEntity):PlayerRow {
    val totals=data.totals(player.id)
    val played=data.fixtures.filter { f->f.homeScore!=null && data.events.any { it.fixtureId==f.id && it.playerId==player.id } }.sortedByDescending { it.kickoff }
    val streak=played.takeWhile { f->data.events.any { it.fixtureId==f.id && it.playerId==player.id && it.kind=="GOAL" } }.size
    val club=data.clubs.find { it.id==player.clubId }
    val leagueClubs=data.clubs.filter { it.leagueId==club?.leagueId }.map { it.id }.toSet()
    val rank=if(totals.goals>0) 1+data.players.count { it.clubId in leagueClubs && data.totals(it.id).goals>totals.goals } else null
    return PlayerRow(player.id,player.name,player.number,data.clubs.find { it.id==player.clubId }?.name.orEmpty(),player.position,
        "${totals.goals} goles · ${totals.assists} asistencias · ${totals.matches} partidos","${totals.yellow} amarillas · ${totals.red} rojas · ${totals.minutes} minutos",
        totals.rating?.let { "Valoración: ${String.format(java.util.Locale.ROOT,"%.1f",it)}" },player.accountId!=null && player.accountId==session.userId,totals.goals,totals.assists,totals.matches,totals.yellow,totals.red,totals.minutes,
        data.events.filter { it.playerId==player.id && it.rating!=null && data.fixtures.any { f->f.id==it.fixtureId && f.homeScore!=null } }.sortedBy { e->data.fixtures.find { it.id==e.fixtureId }?.kickoff }.mapNotNull { it.rating }.takeLast(5),streak,rank)
}
fun LocalState.matchRow(match:FixtureEntity):MatchRow {
    val title="${data.clubs.find { it.id==match.homeId }?.name.orEmpty()} vs ${data.clubs.find { it.id==match.awayId }?.name.orEmpty()}"
    val score=if(match.homeScore==null) "Programado" else "Resultado: ${match.homeScore} – ${match.awayScore}"
    return MatchRow(match.id,title,score,match.homeScore==null,"Jornada ${match.matchday} · ${LocalDateTime.parse(match.kickoff).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",match.venue,"$title\n$score\n${match.kickoff.replace('T',' ')} · ${match.venue}",data.clubs.find { it.id==match.homeId }?.name.orEmpty(),data.clubs.find { it.id==match.awayId }?.name.orEmpty(),if(match.homeScore==null) "VS" else "${match.homeScore} : ${match.awayScore}",LocalDateTime.parse(match.kickoff).toLocalDate())
}
fun eventTitle(kind:String)=when(kind) { "GOAL"->"Gol";"ASSIST"->"Asistencia";"YELLOW"->"Amarilla";"RED"->"Roja";else->"Participación" }
