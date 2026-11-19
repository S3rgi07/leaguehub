package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import java.time.LocalDateTime
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/** All edits check ownership and relationships inside the same transaction. */
class CompetitionRepository(private val database: LeagueHubDatabase) {
    private val dao get() = database.competitionDao()
    val snapshot = combine(dao.allLeagues(), dao.allClubs(), dao.allPlayers(), dao.allFixtures(), dao.allEvents()) { leagues,clubs,players,fixtures,events -> CompetitionSnapshot(leagues,clubs,players,fixtures,events) }.combine(database.userDataDao().allMatchSheets()) { snapshot,rows ->
        snapshot.copy(sheets=rows.mapNotNull { row -> val id=row.dataKey.removePrefix("match-sheet:");val fixture=snapshot.fixtures.find { it.id==id };if(fixture!=null && snapshot.leagues.any { it.id==fixture.leagueId && it.ownerId==row.userId }) runCatching { id to MatchSheetRepository.decode(row.payload) }.getOrNull() else null }.toMap())
    }
    private suspend fun requireOwner(leagueId: String, ownerId: String) {
        require(ownerId.isNotBlank() && dao.league(leagueId)?.ownerId == ownerId) { "No puedes modificar esta liga" }
    }
    suspend fun saveLeague(ownerId: String, league: LeagueEntity) = database.withTransaction {
        require(ownerId.isNotBlank() && league.ownerId == ownerId) { "Usuario inválido" }
        require(league.name.isNotBlank() && league.season.isNotBlank()) { "Completa nombre y temporada" }
        dao.league(league.id)?.let { require(it.ownerId == ownerId) { "No puedes modificar esta liga" } }
        dao.save(league.copy(name = league.name.trim(), season = league.season.trim()))
    }
    suspend fun saveClub(ownerId: String, club: ClubEntity) = database.withTransaction {
        requireOwner(club.leagueId, ownerId)
        require(club.name.isNotBlank()) { "Ingresa el nombre del equipo" }
        dao.club(club.id)?.let { require(it.leagueId == club.leagueId) { "No puedes trasladar el equipo a otra liga" } }
        dao.save(club.copy(name = club.name.trim()))
    }
    suspend fun savePlayer(ownerId: String, player: PlayerEntity) = database.withTransaction {
        val club = requireNotNull(dao.club(player.clubId)) { "Equipo inexistente" }
        requireOwner(club.leagueId, ownerId)
        dao.player(player.id)?.let { existing ->
            requireOwner(requireNotNull(dao.club(existing.clubId)).leagueId, ownerId)
        }
        require(player.name.isNotBlank() && player.position.isNotBlank()) { "Completa nombre y posición" }
        require(player.number in 1..99) { "El dorsal debe estar entre 1 y 99" }
        require(dao.dorsalCount(player.clubId, player.number, player.id) == 0) { "El dorsal ya está ocupado en este equipo" }
        dao.player(player.id)?.let { old ->
            require(old.clubId == player.clubId || (dao.playerEventCount(player.id) == 0 && !hasSheetReference(player.id))) { "No puedes trasladar un jugador con historial de partidos" }
        }
        player.accountId?.let { id ->
            require(database.userAccountDao().findById(id) != null) { "La cuenta vinculada no existe" }
            require(dao.allPlayers().first().none { it.id != player.id && it.accountId == id }) { "Esta cuenta ya tiene un jugador vinculado" }
        }
        dao.save(player.copy(name = player.name.trim(), position = player.position.trim()))
    }
    suspend fun saveFixture(ownerId: String, fixture: FixtureEntity) = database.withTransaction {
        requireOwner(fixture.leagueId, ownerId)
        dao.fixture(fixture.id)?.let { require(it.leagueId == fixture.leagueId) { "No puedes trasladar el partido a otra liga" } }
        require(fixture.homeId != fixture.awayId) { "Selecciona dos equipos distintos" }
        require(dao.club(fixture.homeId)?.leagueId == fixture.leagueId && dao.club(fixture.awayId)?.leagueId == fixture.leagueId) { "Los equipos deben pertenecer a la liga" }
        require(fixture.venue.isNotBlank() && fixture.matchday > 0) { "Completa cancha y jornada" }
        LocalDateTime.parse(fixture.kickoff)
        require((fixture.homeScore == null && fixture.awayScore == null) ||
            (fixture.homeScore != null && fixture.awayScore != null && fixture.homeScore >= 0 && fixture.awayScore >= 0)) { "Ingresa ambos marcadores sin negativos" }
        val events = dao.events(fixture.id)
        dao.fixture(fixture.id)?.let { old ->
            val sheet=MatchSheetRepository.decode(database.userDataDao().read(ownerId,MatchSheetRepository.key(fixture.id)))
            require((events.isEmpty() && sheet.teams.isEmpty() && sheet.substitutions.isEmpty()) || (old.homeId == fixture.homeId && old.awayId == fixture.awayId)) { "Quita los eventos y las alineaciones antes de cambiar los equipos" }
        }
        val previous=dao.fixture(fixture.id)
        if(previous?.homeScore!=null && fixture.homeScore==null) {
            database.userDataDao().read(ownerId,MatchSheetRepository.key(fixture.id))?.let { payload ->
                val reset=MatchSheetRepository.decode(payload).copy(phase="SCHEDULED",minute=0)
                database.userDataDao().upsert(UserDataEntity(ownerId,MatchSheetRepository.key(fixture.id),MatchSheetRepository.json.encodeToString(MatchSheet.serializer(),reset)))
            }
        }
        validateGoals(fixture, events)
        dao.save(fixture.copy(venue = fixture.venue.trim()))
    }

    private suspend fun validateGoals(fixture: FixtureEntity, events: List<MatchEventEntity>) {
        if (fixture.homeScore == null || fixture.awayScore == null) return
        val goalClubs = events.filter { it.kind == "GOAL" }.map { requireNotNull(dao.player(it.playerId)).clubId }
        require(goalClubs.count { it == fixture.homeId } <= fixture.homeScore && goalClubs.count { it == fixture.awayId } <= fixture.awayScore) {
            "El marcador no puede ser menor que los goles registrados"
        }
    }

    suspend fun saveEvent(ownerId: String, event: MatchEventEntity) = database.withTransaction {
        val fixture = requireNotNull(dao.fixture(event.fixtureId)) { "Partido inexistente" }
        requireOwner(fixture.leagueId, ownerId)
        dao.event(event.id)?.let { require(it.fixtureId == event.fixtureId) { "No puedes trasladar un evento" } }
        val player = requireNotNull(dao.player(event.playerId)) { "Jugador inexistente" }
        require(player.clubId == fixture.homeId || player.clubId == fixture.awayId) { "El jugador no participa en este partido" }
        val sheet=MatchSheetRepository.decode(database.userDataDao().read(ownerId,MatchSheetRepository.key(fixture.id)))
        if(event.kind in listOf("GOAL","ASSIST") && sheet.teams.any { it.clubId==player.clubId }) require(player.id in MatchSheetRepository.playersOnCourt(sheet,player.clubId,event.minute)) { "El jugador no estaba en cancha en ese minuto. Revisa su alineación y cambios." }
        require(event.kind in listOf("GOAL", "ASSIST", "YELLOW", "RED", "APPEARANCE")) { "Tipo de evento inválido" }
        require(event.minute in 0..130 && event.minutesPlayed in 0..130) { "Los minutos deben estar entre 0 y 130" }
        require(event.rating == null || (event.rating.isFinite() && event.rating in 0.0..10.0)) { "La valoración debe estar entre 0 y 10" }
        val others = dao.events(fixture.id).filter { it.id != event.id }
        require(event.kind != "APPEARANCE" || others.none { it.playerId == event.playerId && it.kind == "APPEARANCE" }) { "El jugador ya tiene participación registrada" }
        require(event.kind == "APPEARANCE" || (event.minutesPlayed == 0 && event.rating == null)) { "Los minutos y la valoración corresponden a una participación" }
        validateGoals(fixture, others + event)
        dao.save(event)
    }

    private suspend fun hasSheetReference(playerId:String):Boolean = database.userDataDao().allMatchSheets().first().any { row ->
        val sheet=MatchSheetRepository.decode(row.payload)
        sheet.teams.any { playerId in it.starters } || sheet.substitutions.any { it.inId==playerId || it.outId==playerId }
    }
    suspend fun delete(ownerId: String, kind: String, id: String) = database.withTransaction {
        when (kind) {
            "league" -> { val item = requireNotNull(dao.league(id)); requireOwner(id, ownerId); dao.delete(item) }
            "club" -> { val item = requireNotNull(dao.club(id)); requireOwner(item.leagueId, ownerId); dao.delete(item) }
            "player" -> { require(!hasSheetReference(id)) { "Quita al jugador de las alineaciones y cambios antes de eliminarlo" }; val item = requireNotNull(dao.player(id)); requireOwner(requireNotNull(dao.club(item.clubId)).leagueId, ownerId); dao.delete(item) }
            "fixture" -> { val item = requireNotNull(dao.fixture(id)); requireOwner(item.leagueId, ownerId); dao.delete(item);database.userDataDao().delete(ownerId,MatchSheetRepository.key(id)) }
            "event" -> { val item = requireNotNull(dao.event(id)); requireOwner(requireNotNull(dao.fixture(item.fixtureId)).leagueId, ownerId); dao.delete(item) }
            else -> error("Registro inválido")
        }
    }
}
