package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import java.time.LocalDateTime

/** All edits check ownership and relationships inside the same transaction. */
class CompetitionRepository(private val database: LeagueHubDatabase) {
    private val dao get() = database.competitionDao()
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
        dao.save(fixture.copy(venue = fixture.venue.trim()))
    }
}
