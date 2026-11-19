package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.CompetitionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test

class CompetitionDatabaseTest {
    @Test fun storesRelatedDataAndRejectsInvalidMatchesAndOtherOwners() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), LeagueHubDatabase::class.java).build()
        try {
            val repository = CompetitionRepository(db)
            repository.saveLeague("owner", LeagueEntity("league", "owner", "Liga escolar", "2026"))
            repository.saveClub("owner", ClubEntity("home", "league", "Local"))
            repository.saveClub("owner", ClubEntity("away", "league", "Visitante"))
            repository.savePlayer("owner", PlayerEntity("player", "home", "Ana", 10, "Delantera"))
            val match = FixtureEntity("match", "league", "home", "away", "2026-11-20T16:00", "Cancha central", 1)
            repository.saveFixture("owner", match)
            assertEquals(2, db.competitionDao().observeClubs("league").first().size)
            assertEquals("Ana", db.competitionDao().observePlayers("home").first().single().name)
            assertEquals(match, db.competitionDao().observeFixtures("league").first().single())
            try { repository.saveFixture("owner", match.copy(awayId = "home")); fail("Debe rechazar equipos iguales") } catch (_: IllegalArgumentException) { }
            try { repository.saveFixture("other", match.copy(homeScore = 2, awayScore = 0)); fail("Debe rechazar otro propietario") } catch (_: IllegalArgumentException) { }
            assertNull(db.competitionDao().fixture("match")!!.homeScore)
        } finally { db.close() }
    }
}
