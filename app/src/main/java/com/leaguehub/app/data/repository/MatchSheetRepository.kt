package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable data class TeamSheet(val clubId:String, val formation:String="2-2", val starters:List<String> = emptyList())
@Serializable data class Substitution(val clubId:String,val outId:String,val inId:String,val minute:Int)
@Serializable data class MatchSheet(val teams:List<TeamSheet> = emptyList(),val substitutions:List<Substitution> = emptyList(),val photos:List<String> = emptyList(),val phase:String="SCHEDULED",val minute:Int=0)

/** Match-specific documents live in Room's user_data table, under the league owner's key. */
class MatchSheetRepository(private val database:LeagueHubDatabase) {
    companion object {
        fun key(matchId:String)="match-sheet:$matchId"
        val json=Json { ignoreUnknownKeys=true; encodeDefaults=true }
        fun decode(text:String?)=text?.let { json.decodeFromString<MatchSheet>(it) } ?: MatchSheet()
        fun playersOnCourt(sheet:MatchSheet,clubId:String,minute:Int=130):List<String> {
            val current=sheet.teams.find { it.clubId==clubId }?.starters.orEmpty().toMutableList()
            sheet.substitutions.filter { it.clubId==clubId && it.minute<=minute }.forEach { sub ->
                val index=current.indexOf(sub.outId)
                require(index>=0 && sub.inId !in current) { "El cambio no corresponde a los jugadores en cancha" }
                current[index]=sub.inId
            }
            return current
        }
        fun minutes(sheet:MatchSheet,clubId:String,duration:Int):Map<String,Int> {
            require(duration in 1..130) { "La duración debe estar entre 1 y 130 minutos" }
            val entered=sheet.teams.find { it.clubId==clubId }?.starters.orEmpty().associateWith { 0 }.toMutableMap()
            val totals=mutableMapOf<String,Int>()
            sheet.substitutions.filter { it.clubId==clubId }.forEach { sub ->
                require(sub.minute<=duration) { "Hay cambios posteriores a la duración indicada" }
                val start=requireNotNull(entered.remove(sub.outId)) { "El jugador que sale no está en cancha" }
                totals[sub.outId]=(totals[sub.outId] ?: 0)+sub.minute-start
                require(sub.inId !in entered) { "El jugador que entra ya está en cancha" }
                entered[sub.inId]=sub.minute
            }
            entered.forEach { (id,start) -> totals[id]=(totals[id] ?: 0)+duration-start }
            return totals
        }
        fun validate(sheet:MatchSheet,fixture:FixtureEntity,players:List<PlayerEntity>) {
            require(sheet.phase in listOf("SCHEDULED","LIVE","HALFTIME") && sheet.minute in 0..130) { "Estado de partido inválido" }
            require(sheet.teams.size<=2 && sheet.teams.map { it.clubId }.distinct().size==sheet.teams.size) { "Equipos duplicados" }
            sheet.teams.forEach { team ->
                require(team.clubId in listOf(fixture.homeId,fixture.awayId)) { "El equipo no pertenece al partido" }
                val size=when(team.formation) { "2-2"->5;"2-3-1"->7;"4-3-3"->11;else->error("Formación inválida") }
                require(team.starters.isNotEmpty() && team.starters.size<=size && team.starters.distinct().size==team.starters.size) { "Revisa la cantidad de titulares" }
                require(team.starters.all { id -> players.any { it.id==id && it.clubId==team.clubId } }) { "Un titular no pertenece a la plantilla" }
            }
            require(sheet.substitutions.size<=200 && sheet.photos.size<=30 && sheet.photos.distinct().size==sheet.photos.size) { "Demasiados cambios o fotos duplicadas" }
            sheet.photos.forEach { require(it.startsWith("content://") && it.length<=4096) { "Imagen inválida" } }
            sheet.substitutions.groupBy { it.clubId }.forEach { (club,subs) ->
                require(sheet.teams.any { it.clubId==club }) { "Guarda los titulares antes de hacer cambios" }
                require(subs.zipWithNext().all { (a,b)->a.minute<=b.minute }) { "Registra los cambios en orden de minuto" }
                subs.forEach { sub -> require(sub.minute in 0..130 && sub.outId!=sub.inId && listOf(sub.outId,sub.inId).all { id->players.any { it.id==id && it.clubId==club } }) { "Sustitución inválida" } }
                playersOnCourt(sheet,club)
            }
        }
    }
    fun observe(owner:String,matchId:String)=database.userDataDao().observe(owner,key(matchId)).map(::decode)
    suspend fun update(actor:String,matchId:String,change:(MatchSheet)->MatchSheet) = database.withTransaction {
        val fixture=requireNotNull(database.competitionDao().fixture(matchId)) { "Partido inexistente" }
        val league=requireNotNull(database.competitionDao().league(fixture.leagueId))
        require(actor==league.ownerId) { "Solo el organizador de esta liga puede modificar el partido" }
        val next=change(decode(database.userDataDao().read(actor,key(matchId))))
        validate(next,fixture,database.competitionDao().allPlayers().first())
        database.userDataDao().upsert(UserDataEntity(actor,key(matchId),json.encodeToString(next)))
    }
    suspend fun applyMinutes(actor:String,matchId:String,duration:Int) = database.withTransaction {
        val fixture=requireNotNull(database.competitionDao().fixture(matchId))
        require(database.competitionDao().league(fixture.leagueId)?.ownerId==actor) { "No puedes modificar esta liga" }
        val sheet=decode(database.userDataDao().read(actor,key(matchId)))
        require(sheet.teams.isNotEmpty()) { "Guarda una alineación antes de calcular minutos" }
        val repository=CompetitionRepository(database)
        val events=database.competitionDao().events(matchId)
        sheet.teams.forEach { team -> minutes(sheet,team.clubId,duration).forEach { (player,total) ->
            val existing=events.find { it.playerId==player && it.kind=="APPEARANCE" }
            repository.saveEvent(actor,MatchEventEntity(existing?.id ?: java.util.UUID.randomUUID().toString(),matchId,player,"APPEARANCE",duration,total,existing?.rating))
        } }
    }
}
