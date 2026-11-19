package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import com.leaguehub.app.data.local.*
import java.time.LocalDate

/** A persistent, editable local example, provisioned only when its published login is used. */
object DemoAccount {
    const val EMAIL = "demo@leaguehub.app"
    const val PASSWORD = "DemoLiga2026!"
    const val USER_ID = "leaguehub-demo-owner-v1"
    const val LEAGUE_ID = "leaguehub-demo-league-v1"

    suspend fun ensureCreated(database: LeagueHubDatabase, today: LocalDate = LocalDate.now()) {
        database.withTransaction {
            // Never reset a password or restore data the user has edited/deleted.
            if (database.userAccountDao().findByEmail(EMAIL) != null) return@withTransaction
            val salt = PasswordHasher.newSalt()
            database.userAccountDao().insert(UserAccountEntity(
                USER_ID, "Ana Torres", EMAIL, PasswordHasher.hash(PASSWORD, salt), salt,
                role = "ORGANIZER", selectedLeagueId = LEAGUE_ID, onboardingCompleted = true
            ))
            val repository = CompetitionRepository(database)
            repository.saveLeague(USER_ID, LeagueEntity(LEAGUE_ID, USER_ID, "Liga Estelar · Demo", "Temporada ${today.year}"))
            val names = listOf(
                listOf("Ana Torres", "Diego Ruiz", "Valeria Cruz", "Mateo Silva", "Lucía Reyes", "Pablo León"),
                listOf("Carlos Vega", "María Luna", "Andrés Ríos", "Elena Soto", "Jorge Lara", "Sara Ortiz"),
                listOf("Daniel Sol", "Paula Gil", "Luis Cano", "Irene Paz", "Hugo Mora", "Alma Vidal"),
                listOf("David Arias", "Clara Díaz", "Iván Peña", "Laura Sáez", "Bruno Rosas", "Eva Nieto"),
                listOf("Álex Costa", "Julia Ramos", "Nicolás Rey", "Marta Valle", "Leo Fuentes", "Inés Castro"),
                listOf("Samuel Flores", "Celia Núñez", "Óscar Bravo", "Alicia Campos", "Mario Ponce", "Nora Medina")
            )
            val clubs = listOf("Falcons", "Titanes", "Lobos", "Rayos", "Cóndores", "Atléticos").mapIndexed { index, name ->
                ClubEntity("demo-club-$index", LEAGUE_ID, name).also { repository.saveClub(USER_ID, it) }
            }
            val rosters = clubs.mapIndexed { team, club ->
                names[team].mapIndexed { index, name ->
                    PlayerEntity("demo-player-$team-$index", club.id, name, listOf(10, 7, 9, 4, 6, 1)[index],
                        listOf("Delantera", "Mediocampista", "Delantero", "Defensa", "Defensa", "Portero")[index],
                        if (team == 0 && index == 0) USER_ID else null
                    ).also { repository.savePlayer(USER_ID, it) }
                }
            }
            var rotation = clubs.indices.toList()
            repeat(7) { round ->
                repeat(3) { pair ->
                    val first = rotation[pair]
                    val second = rotation[5 - pair]
                    val home = if (round % 2 == 0) first else second
                    val away = if (round % 2 == 0) second else first
                    val completed = round < 5
                    val date = if (completed) today.minusDays(((5 - round) * 3).toLong()) else today.plusDays((1 + (round - 5) * 3).toLong())
                    val fixture = FixtureEntity("demo-match-$round-$pair", LEAGUE_ID, clubs[home].id, clubs[away].id,
                        "${date}T${16 + pair}:00", listOf("Estadio Municipal", "Cancha Central", "Parque Deportivo")[pair], round + 1,
                        if (completed) 2 + (round + pair) % 2 else null, if (completed) 1 + pair % 2 else null)
                    repository.saveFixture(USER_ID, fixture)
                    if (completed) {
                        listOf(home to fixture.homeScore!!, away to fixture.awayScore!!).forEach { (team, goals) ->
                            val roster = rosters[team]
                            repeat(goals) { goal ->
                                repository.saveEvent(USER_ID, MatchEventEntity("${fixture.id}-$team-goal-$goal", fixture.id, roster[goal].id, "GOAL", 18 + goal * 17))
                                repository.saveEvent(USER_ID, MatchEventEntity("${fixture.id}-$team-assist-$goal", fixture.id, roster[if (goal == 0) 1 else 0].id, "ASSIST", 18 + goal * 17))
                            }
                            roster.forEachIndexed { index, player ->
                                val rating = if (team == 0 && index == 0) listOf(8.1, 7.4, 8.8, 8.3, 9.0)[round] else 6.5 + (round + index) % 6 * .4
                                repository.saveEvent(USER_ID, MatchEventEntity("${fixture.id}-$team-app-$index", fixture.id, player.id, "APPEARANCE", 90, if (index < 3) 90 else 65, rating))
                            }
                            if (round == 3 && team == away) repository.saveEvent(USER_ID, MatchEventEntity("${fixture.id}-$team-red", fixture.id, roster.last().id, "RED", 65))
                            if (round % 2 == 0) repository.saveEvent(USER_ID, MatchEventEntity("${fixture.id}-$team-yellow", fixture.id, roster[0].id, "YELLOW", 68))
                        }
                    }
                }
                rotation = listOf(rotation.first(), rotation.last()) + rotation.subList(1, 5)
            }
            database.userDataDao().upsert(UserDataEntity(USER_ID, "followed_teams", "[\"${clubs[0].id}\",\"${clubs[1].id}\"]"))
        }
    }
    /** One-time upgrade for an existing demo account; never reapplies a user-edited acta. */
    suspend fun ensureSheets(database:LeagueHubDatabase) {
        database.withTransaction {
            val dao=database.userDataDao()
            if(dao.read(USER_ID,"demo-sheets-v1")!=null || database.userAccountDao().findByEmail(EMAIL)?.userId!=USER_ID) return@withTransaction
            val players=database.competitionDao().allPlayers().first()
            val fixtures=database.competitionDao().allFixtures().first().filter { it.leagueId==LEAGUE_ID }
            val repository=MatchSheetRepository(database)
            fixtures.forEach { fixture ->
                if(dao.read(USER_ID,MatchSheetRepository.key(fixture.id))==null) {
                    val teams=listOf(fixture.homeId,fixture.awayId).mapNotNull { club ->
                        val roster=players.filter { it.clubId==club }.sortedBy { it.id }
                        val keeper=roster.find { it.position.contains("porter",true) }
                        val starters=listOfNotNull(keeper)+roster.filter { it.id!=keeper?.id }.take(4)
                        if(starters.size==5) TeamSheet(club,"2-2",starters.map { it.id }) else null
                    }
                    repository.update(USER_ID,fixture.id) { it.copy(teams=teams) }
                }
            }
            dao.upsert(UserDataEntity(USER_ID,"demo-sheets-v1","true"))
        }
    }

}
