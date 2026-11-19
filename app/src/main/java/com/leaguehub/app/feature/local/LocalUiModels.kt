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
data class PlayerRow(val id:String,val name:String,val number:Int,val team:String,val position:String,val stats:String,val discipline:String,val rating:String?,val isMe:Boolean)
data class MatchRow(val id:String,val title:String,val score:String,val scheduled:Boolean,val date:String,val venue:String,val shareText:String)
data class EventRow(val id:String,val title:String,val player:String,val details:String?)
data class PositionRow(val id:String,val title:String,val numbers:String)
fun LocalState.playerRow(player:PlayerEntity):PlayerRow {
    val totals=data.totals(player.id)
    return PlayerRow(player.id,player.name,player.number,data.clubs.find { it.id==player.clubId }?.name.orEmpty(),player.position,
        "${totals.goals} goles · ${totals.assists} asistencias · ${totals.matches} partidos","${totals.yellow} amarillas · ${totals.red} rojas · ${totals.minutes} minutos",
        totals.rating?.let { "Valoración: ${String.format(java.util.Locale.ROOT,"%.1f",it)}" },player.accountId!=null && player.accountId==session.userId)
}
fun LocalState.matchRow(match:FixtureEntity):MatchRow {
    val title="${data.clubs.find { it.id==match.homeId }?.name.orEmpty()} vs ${data.clubs.find { it.id==match.awayId }?.name.orEmpty()}"
    val score=if(match.homeScore==null) "Programado" else "Resultado: ${match.homeScore} – ${match.awayScore}"
    return MatchRow(match.id,title,score,match.homeScore==null,"Jornada ${match.matchday} · ${LocalDateTime.parse(match.kickoff).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",match.venue,"$title\n$score\n${match.kickoff.replace('T',' ')} · ${match.venue}")
}
fun eventTitle(kind:String)=when(kind) { "GOAL"->"Gol";"ASSIST"->"Asistencia";"YELLOW"->"Amarilla";"RED"->"Roja";else->"Participación" }
