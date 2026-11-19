package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DemoAccountTest {
    @Test fun publishedLoginCreatesACompleteLeagueAndKeepsUserEdits() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, LeagueHubDatabase::class.java).build()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val sessions = UserSessionRepository(db.userSessionDao(), scope, db.userAccountDao())
            sessions.restoreCompleted.first { it }
            val auth = AuthRepository(db, sessions)
            val repo = CompetitionRepository(db)
            val today = LocalDate.now()
            auth.login("  DEMO@leaguehub.app  ", DemoAccount.PASSWORD)
            assertEquals(DemoAccount.USER_ID, sessions.session.value.userId)
            assertEquals(SessionRole.ORGANIZER, sessions.session.value.role)
            assertEquals(DemoAccount.LEAGUE_ID, sessions.session.value.selectedLeagueId)
            val data = repo.snapshot.first { it.fixtures.size == 21 && it.events.isNotEmpty() }
            assertEquals(1, data.leagues.size)
            assertEquals(6, data.clubs.size)
            assertEquals(36, data.players.size)
            assertEquals(15, data.fixtures.count { it.homeScore != null })
            assertEquals(6, data.fixtures.count { it.homeScore == null })
            val player = data.players.single { it.accountId == DemoAccount.USER_ID }
            val totals = data.totals(player.id)
            assertEquals(5, totals.matches)
            assertEquals(5, totals.goals)
            assertEquals(3, totals.assists)
            assertTrue(data.events.any { it.kind == "RED" })
            assertEquals(3, totals.yellow)
            assertEquals(450, totals.minutes)
            assertEquals(5, data.events.count { it.playerId == player.id && it.rating != null })
            assertTrue(data.table(DemoAccount.LEAGUE_ID).all { it.played == 5 })
            assertTrue(data.fixtures.filter { it.homeScore == null }.all { LocalDate.parse(it.kickoff.substringBefore('T')) > today })
            // Every goal in every final score has a corresponding persisted event.
            data.fixtures.filter { it.homeScore != null }.forEach { fixture ->
                val goals = data.events.filter { it.fixtureId == fixture.id && it.kind == "GOAL" }
                assertEquals(fixture.homeScore, goals.count { e -> data.players.first { it.id == e.playerId }.clubId == fixture.homeId })
                assertEquals(fixture.awayScore, goals.count { e -> data.players.first { it.id == e.playerId }.clubId == fixture.awayId })
            }
            val club = data.clubs.first()
            repo.saveClub(DemoAccount.USER_ID, club.copy(name = "Equipo editado"))
            val removedFixture = data.fixtures.first { it.homeScore == null }
            repo.delete(DemoAccount.USER_ID, "fixture", removedFixture.id)
            auth.updateName("Mi nombre de prueba")
            auth.changePassword(DemoAccount.PASSWORD, "NuevaDemo2026!")
            sessions.logout()
            val incorrect = runCatching { auth.login(DemoAccount.EMAIL, DemoAccount.PASSWORD) }
            assertTrue(incorrect.isFailure)
            auth.login(DemoAccount.EMAIL, "NuevaDemo2026!")
            assertEquals("Mi nombre de prueba", sessions.session.value.userName)
            val edited = repo.snapshot.first { it.fixtures.size == 20 }
            assertEquals("Equipo editado", edited.clubs.first { it.id == club.id }.name)
            assertFalse(edited.fixtures.any { it.id == removedFixture.id })
        } finally { scope.cancel(); db.close() }
    }
}
