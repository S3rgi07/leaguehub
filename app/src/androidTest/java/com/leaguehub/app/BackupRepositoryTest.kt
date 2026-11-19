package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test

class BackupRepositoryTest {
    private suspend fun reject(block: suspend () -> Unit) { try { block();fail("Se esperaba rechazar el archivo") } catch(_: IllegalArgumentException) {} }
    @Test fun roundTripKeepsSportsDataWithoutAccountsAndAlwaysCreatesIndependentCopies()=runBlocking {
        val db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),LeagueHubDatabase::class.java).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());sessions.restoreCompleted.first { it }
            val auth=AuthRepository(db,sessions)
            auth.register("Origen","source@prueba.com","Prueba123");val source=sessions.session.value.userId!!
            val competitions=CompetitionRepository(db)
            competitions.saveLeague(source,LeagueEntity("l",source,"Liga Águilas","2026"))
            competitions.saveClub(source,ClubEntity("a","l","Águilas"));competitions.saveClub(source,ClubEntity("b","l","Bravos"))
            competitions.savePlayer(source,PlayerEntity("p","a","María",10,"Delantera",source))
            competitions.saveFixture(source,FixtureEntity("f","l","a","b","2026-11-20T16:00","Central",1,1,0))
            competitions.saveEvent(source,MatchEventEntity("e","f","p","GOAL",10))
            auth.register("Destino","target@prueba.com","Prueba123");val target=sessions.session.value.userId!!
            competitions.saveLeague(target,LeagueEntity("other",target,"Otra liga","2026"))
            val backups=BackupRepository(db)
            val text=backups.export(source)
            assertFalse(text.contains("source@prueba.com"));assertFalse(text.contains("Prueba123"));assertFalse(text.contains("password"));assertFalse(text.contains("accountId"));assertFalse(text.contains("ownerId"))
            val document=backups.read(text.byteInputStream(Charsets.UTF_8))
            assertEquals(1,document.leagues.size);assertEquals("Liga Águilas",document.leagues.single().name)
            assertEquals(1,backups.restore(target,document));assertEquals(1,backups.restore(target,document))
            val restored=competitions.snapshot.first()
            assertEquals(4,restored.leagues.size)
            val copies=restored.leagues.filter { it.ownerId==target && it.name.endsWith("(copia)") }
            assertEquals(2,copies.size)
            copies.forEach { league ->
                assertNotEquals("l",league.id)
                assertEquals(3,restored.table(league.id).first().points)
                val copiedPlayer=restored.players.first { p -> restored.clubs.any { it.id==p.clubId && it.leagueId==league.id } }
                assertNull(copiedPlayer.accountId);assertEquals(1,restored.totals(copiedPlayer.id).goals)
            }
            assertEquals(source,restored.players.first { it.id=="p" }.accountId)
        } finally { scope.cancel();db.close() }
    }
    @Test fun malformedReferencesAndWriteFailuresNeverLeavePartialImports()=runBlocking {
        val db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),LeagueHubDatabase::class.java).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());sessions.restoreCompleted.first { it }
            AuthRepository(db,sessions).register("Destino","backup@prueba.com","Prueba123")
            val owner=sessions.session.value.userId!!
            val backups=BackupRepository(db)
            val document=BackupDocument("leaguehub-backup",1,"2026-11-18T20:00:00Z",listOf(BackupLeague("l","Liga","2026")),listOf(BackupClub("a","l","Atlas")),emptyList(),emptyList(),emptyList())
            reject { backups.decode("{}") };reject { backups.decode("no es json") }
            reject { backups.restore(owner,document.copy(version=2)) }
            reject { backups.restore(owner,document.copy(clubs=listOf(BackupClub("a","missing","Atlas")))) }
            reject { backups.restore(owner,document.copy(leagues=document.leagues+document.leagues)) }
            reject { backups.restore(owner,document.copy(players=listOf(BackupPlayer("p","a","Ana",-1,"Defensa")))) }
            reject { backups.restore(owner,document.copy(events=listOf(BackupEvent("e","missing","p","GOAL",10,0,null)))) }
            reject { backups.read(byteArrayOf(0xC3.toByte(),0x28).inputStream()) }
            reject { backups.read(ByteArray(BackupRepository.MAX_BYTES+1) { 32 }.inputStream()) }
            assertTrue(db.competitionDao().allLeagues().first().isEmpty())
            db.openHelper.writableDatabase.execSQL("CREATE TRIGGER reject_backup BEFORE INSERT ON clubs BEGIN SELECT RAISE(ABORT, 'test rollback'); END")
            var failed=false
            try { backups.restore(owner,document) } catch(_: android.database.sqlite.SQLiteException) { failed=true }
            assertTrue(failed)
            assertTrue(db.competitionDao().allLeagues().first().isEmpty())
            assertTrue(db.competitionDao().allClubs().first().isEmpty())
        } finally { scope.cancel();db.close() }
    }
}
