package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class LocalCompetitionPersistenceTest {
    private suspend fun reject(block: suspend () -> Unit) { try { block();fail("Debía rechazar la operación") } catch(_: IllegalArgumentException) {} }
    @Test fun resultsEventsProfileLinkAndCorrectionsSurviveDatabaseReopen()= runBlocking {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val name="local-flow-${UUID.randomUUID()}.db"
        var db=Room.databaseBuilder(context,LeagueHubDatabase::class.java,name).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao())
            sessions.restoreCompleted.first { it }
            val auth=AuthRepository(db,sessions)
            auth.register("Ana López","ana@prueba.com","Prueba123")
            val accountId=sessions.session.value.userId!!
            val repo=CompetitionRepository(db)
            repo.saveLeague(accountId,LeagueEntity("l",accountId,"Liga de prueba","2026"))
            repo.saveClub(accountId,ClubEntity("a","l","Atlas"))
            repo.saveClub(accountId,ClubEntity("b","l","Bravos"))
            repo.savePlayer(accountId,PlayerEntity("p","a","Ana López",10,"Delantera",accountId))
            reject { repo.savePlayer(accountId,PlayerEntity("p2","a","Otra",10,"Defensa")) }
            val match=FixtureEntity("m","l","a","b","2026-11-20T16:00","Central",1)
            repo.saveFixture(accountId,match)
            repo.saveEvent(accountId,MatchEventEntity("g","m","p","GOAL",8))
            repo.saveEvent(accountId,MatchEventEntity("ap","m","p","APPEARANCE",0,90,8.5))
            reject { repo.saveEvent(accountId,MatchEventEntity("ap2","m","p","APPEARANCE",0,90)) }
            reject { repo.saveFixture(accountId,match.copy(homeScore=0,awayScore=0)) }
            repo.saveFixture(accountId,match.copy(homeScore=1,awayScore=0))
            reject { repo.saveEvent("other",MatchEventEntity("foreign","m","p","YELLOW",10)) }
            reject { repo.savePlayer(accountId,PlayerEntity("p","b","Ana López",10,"Delantera",accountId)) }
            scope.coroutineContext.cancelChildren()
            db.close()
            db=Room.databaseBuilder(context,LeagueHubDatabase::class.java,name).build()
            val restoredRepo=CompetitionRepository(db)
            var restored=restoredRepo.snapshot.first()
            assertEquals(accountId,restored.players.single().accountId)
            assertEquals(3,restored.table("l").first().points)
            assertEquals(1,restored.totals("p").goals)
            assertEquals(90,restored.totals("p").minutes)
            restoredRepo.delete(accountId,"event","g")
            restoredRepo.saveFixture(accountId,match.copy(homeScore=0,awayScore=1))
            restored=restoredRepo.snapshot.first()
            assertEquals("b",restored.table("l").first().club.id)
            assertEquals(0,restored.totals("p").goals)
            restoredRepo.delete(accountId,"fixture","m")
            restored=restoredRepo.snapshot.first()
            assertTrue(restored.events.isEmpty())
            assertEquals(0,restored.table("l").first().played)
            assertEquals(PlayerTotals(),restored.totals("p"))
            restoredRepo.delete(accountId,"player","p")
            restoredRepo.delete(accountId,"club","a")
            restoredRepo.delete(accountId,"club","b")
            restoredRepo.delete(accountId,"league","l")
            assertTrue(restoredRepo.snapshot.first().leagues.isEmpty())
        } finally { scope.cancel();db.close();context.deleteDatabase(name) }
    }
}
