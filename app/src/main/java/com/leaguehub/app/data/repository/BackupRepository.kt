package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import java.util.UUID

@Serializable data class BackupLeague(val id: String, val name: String, val season: String)
@Serializable data class BackupClub(val id: String, val leagueId: String, val name: String)
@Serializable data class BackupPlayer(val id: String, val clubId: String, val name: String, val number: Int, val position: String)
@Serializable data class BackupFixture(val id: String, val leagueId: String, val homeId: String, val awayId: String, val kickoff: String, val venue: String, val matchday: Int, val homeScore: Int?, val awayScore: Int?)
@Serializable data class BackupEvent(val id: String, val fixtureId: String, val playerId: String, val kind: String, val minute: Int, val minutesPlayed: Int, val rating: Double?)
@Serializable data class BackupDocument(val format: String, val version: Int, val exportedAt: String,
    val leagues: List<BackupLeague>, val clubs: List<BackupClub>, val players: List<BackupPlayer>, val fixtures: List<BackupFixture>, val events: List<BackupEvent>)

/** Sports data only. Account ownership and player-account links never cross the backup boundary. */
class BackupRepository(private val database: LeagueHubDatabase) {
    companion object { const val MAX_BYTES = 8 * 1024 * 1024; const val MAX_RECORDS = 50_000 }
    private val json=Json { prettyPrint=true; ignoreUnknownKeys=false; isLenient=false }
    suspend fun export(ownerId: String): String = withContext(Dispatchers.IO) {
        require(ownerId.isNotBlank()) { "Inicia sesión para exportar" }
        val document=database.withTransaction {
            val dao=database.competitionDao()
            val leagues=dao.allLeagues().first().filter { it.ownerId==ownerId }
            val leagueIds=leagues.map { it.id }.toSet()
            val clubs=dao.allClubs().first().filter { it.leagueId in leagueIds }
            val clubIds=clubs.map { it.id }.toSet()
            val players=dao.allPlayers().first().filter { it.clubId in clubIds }
            val fixtures=dao.allFixtures().first().filter { it.leagueId in leagueIds }
            val fixtureIds=fixtures.map { it.id }.toSet()
            BackupDocument("leaguehub-backup",1,OffsetDateTime.now().toString(),
                leagues.map { BackupLeague(it.id,it.name,it.season) },clubs.map { BackupClub(it.id,it.leagueId,it.name) },
                players.map { BackupPlayer(it.id,it.clubId,it.name,it.number,it.position) },
                fixtures.map { BackupFixture(it.id,it.leagueId,it.homeId,it.awayId,it.kickoff,it.venue,it.matchday,it.homeScore,it.awayScore) },
                dao.allEvents().first().filter { it.fixtureId in fixtureIds }.map { BackupEvent(it.id,it.fixtureId,it.playerId,it.kind,it.minute,it.minutesPlayed,it.rating) })
        }
        validate(document)
        json.encodeToString(document).also { require(it.toByteArray(Charsets.UTF_8).size<=MAX_BYTES) { "El respaldo supera el límite de 8 MB" } }
    }
    suspend fun read(input: InputStream): BackupDocument = withContext(Dispatchers.IO) {
        val output=ByteArrayOutputStream()
        val buffer=ByteArray(8192)
        var total=0
        while(true) {
            val count=input.read(buffer)
            if(count<0) break
            total+=count
            require(total<=MAX_BYTES) { "El archivo supera el límite de 8 MB" }
            output.write(buffer,0,count)
        }
        val text=try { Charsets.UTF_8.newDecoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
            .decode(java.nio.ByteBuffer.wrap(output.toByteArray())).toString() }
            catch (_: java.nio.charset.CharacterCodingException) { throw IllegalArgumentException("El archivo debe estar codificado en UTF-8") }
        decode(text)
    }
    fun decode(text: String): BackupDocument {
        require(text.toByteArray(Charsets.UTF_8).size<=MAX_BYTES) { "El archivo supera el límite de 8 MB" }
        val document=try { json.decodeFromString<BackupDocument>(text) }
            catch (_: IllegalArgumentException) { throw IllegalArgumentException("El archivo no es un respaldo JSON válido de LeagueHub") }
        validate(document)
        return document
    }
    fun validate(d: BackupDocument) {
        require(d.format=="leaguehub-backup" && d.version==1) { "Formato o versión de respaldo no compatible" }
        require(d.leagues.size.toLong()+d.clubs.size+d.players.size+d.fixtures.size+d.events.size<=MAX_RECORDS) { "El respaldo contiene demasiados registros" }
        try { OffsetDateTime.parse(d.exportedAt) } catch (_: Exception) { throw IllegalArgumentException("Fecha de respaldo inválida") }
        fun text(value: String, max: Int=MAX_BYTES) { require(value.isNotBlank() && value.length<=max) { "Hay campos vacíos o demasiado largos" } }
        fun ids(values: List<String>) { values.forEach { text(it,128) };require(values.distinct().size==values.size) { "Hay identificadores duplicados en el respaldo" } }
        ids(d.leagues.map { it.id });ids(d.clubs.map { it.id });ids(d.players.map { it.id });ids(d.fixtures.map { it.id });ids(d.events.map { it.id })
        val leagues=d.leagues.associateBy { it.id };val clubs=d.clubs.associateBy { it.id };val players=d.players.associateBy { it.id };val fixtures=d.fixtures.associateBy { it.id }
        d.leagues.forEach { text(it.name);text(it.season) }
        d.clubs.forEach { text(it.name);require(it.leagueId in leagues) { "Un equipo hace referencia a una liga inexistente" } }
        d.players.forEach { text(it.name);text(it.position);require(it.clubId in clubs && it.number in 1..99) { "Equipo o dorsal de jugador inválido" } }
        require(d.players.map { it.clubId to it.number }.distinct().size==d.players.size) { "Hay dorsales repetidos en un equipo" }
        d.fixtures.forEach { f ->
            require(f.leagueId in leagues && f.homeId!=f.awayId && clubs[f.homeId]?.leagueId==f.leagueId && clubs[f.awayId]?.leagueId==f.leagueId) { "Un partido tiene equipos o liga inválidos" }
            text(f.venue);require(f.matchday>0) { "Jornada inválida" }
            try { java.time.LocalDateTime.parse(f.kickoff) } catch (_: Exception) { throw IllegalArgumentException("Fecha de partido inválida") }
            require((f.homeScore==null && f.awayScore==null) || (f.homeScore!=null && f.awayScore!=null && f.homeScore>=0 && f.awayScore>=0)) { "Marcador inválido" }
        }
        d.events.forEach { e ->
            val f=fixtures[e.fixtureId];val p=players[e.playerId]
            require(f!=null && p!=null && (p.clubId==f.homeId || p.clubId==f.awayId)) { "Un evento no corresponde al partido o al jugador" }
            require(e.kind in listOf("GOAL","ASSIST","YELLOW","RED","APPEARANCE") && e.minute in 0..130 && e.minutesPlayed in 0..130) { "Evento o minutos inválidos" }
            require(e.rating==null || (e.rating.isFinite() && e.rating in 0.0..10.0)) { "Valoración inválida" }
            require(e.kind=="APPEARANCE" || (e.minutesPlayed==0 && e.rating==null)) { "Participación inválida" }
        }
        val appearances=d.events.filter { it.kind=="APPEARANCE" }
        require(appearances.map { it.fixtureId to it.playerId }.distinct().size==appearances.size) { "Hay participaciones duplicadas" }
        val goals=d.events.filter { it.kind=="GOAL" }.groupingBy { it.fixtureId to players.getValue(it.playerId).clubId }.eachCount()
        d.fixtures.forEach { f -> if(f.homeScore!=null && f.awayScore!=null) require((goals[f.id to f.homeId] ?: 0)<=f.homeScore && (goals[f.id to f.awayId] ?: 0)<=f.awayScore) { "Hay más goles registrados que en el marcador" } }
    }
    suspend fun restore(ownerId: String, document: BackupDocument): Int = withContext(Dispatchers.IO) {
        validate(document)
        database.withTransaction {
            require(database.userAccountDao().findById(ownerId)!=null) { "Inicia sesión con una cuenta local válida" }
            fun map(ids: List<String>)=ids.associateWith { UUID.randomUUID().toString() }
            val leagues=map(document.leagues.map { it.id });val clubs=map(document.clubs.map { it.id });val players=map(document.players.map { it.id });val fixtures=map(document.fixtures.map { it.id })
            val dao=database.competitionDao()
            document.leagues.forEach { dao.save(LeagueEntity(leagues.getValue(it.id),ownerId,"${it.name} (copia)",it.season)) }
            document.clubs.forEach { dao.save(ClubEntity(clubs.getValue(it.id),leagues.getValue(it.leagueId),it.name)) }
            document.players.forEach { dao.save(PlayerEntity(players.getValue(it.id),clubs.getValue(it.clubId),it.name,it.number,it.position,accountId=null)) }
            document.fixtures.forEach { dao.save(FixtureEntity(fixtures.getValue(it.id),leagues.getValue(it.leagueId),clubs.getValue(it.homeId),clubs.getValue(it.awayId),it.kickoff,it.venue,it.matchday,it.homeScore,it.awayScore)) }
            document.events.forEach { dao.save(MatchEventEntity(UUID.randomUUID().toString(),fixtures.getValue(it.fixtureId),players.getValue(it.playerId),it.kind,it.minute,it.minutesPlayed,it.rating)) }
            document.leagues.size
        }
    }
}
