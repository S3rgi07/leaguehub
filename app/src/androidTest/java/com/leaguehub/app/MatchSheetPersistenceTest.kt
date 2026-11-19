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
class MatchSheetPersistenceTest {
 @Test fun sheetMinutesLiveStateAndBackupSurviveReopeningAndProtectReferences() = runBlocking {
  val context=ApplicationProvider.getApplicationContext<Context>()
  val name="sheet-${UUID.randomUUID()}.db"
  fun open()=Room.databaseBuilder(context,LeagueHubDatabase::class.java,name).build()
  var db=open();val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  try {
   val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());sessions.restoreCompleted.first { it }
   val auth=AuthRepository(db,sessions);auth.login(DemoAccount.EMAIL,DemoAccount.PASSWORD)
   val owner=DemoAccount.USER_ID
   var data=CompetitionRepository(db).snapshot.first { it.fixtures.size==21 && it.sheets.size==21 }
   val fixture=data.fixtures.first { it.homeScore!=null };val sheet=data.sheets.getValue(fixture.id);val team=sheet.teams.first()
   val bench=data.players.first { it.clubId==team.clubId && it.id !in team.starters }
   val sheets=MatchSheetRepository(db)
   sheets.update(owner,fixture.id) { it.copy(substitutions=listOf(Substitution(team.clubId,team.starters.last(),bench.id,65))) }
   sheets.applyMinutes(owner,fixture.id,90)
   data=CompetitionRepository(db).snapshot.first { s->s.events.any { it.fixtureId==fixture.id && it.playerId==bench.id && it.kind=="APPEARANCE" && it.minutesPlayed==25 } }
   assertEquals(65,data.events.first { it.fixtureId==fixture.id && it.playerId==team.starters.last() && it.kind=="APPEARANCE" }.minutesPlayed)
   assertTrue(runCatching { CompetitionRepository(db).delete(owner,"player",bench.id) }.isFailure)
   assertTrue(runCatching { sheets.update("another-owner",fixture.id) { MatchSheet() } }.isFailure)
   val upcoming=data.fixtures.first { it.homeScore==null }
   sheets.update(owner,upcoming.id) { it.copy(phase="LIVE",minute=32) }
   val competition=CompetitionRepository(db)
   competition.saveFixture(owner,upcoming.copy(homeScore=1,awayScore=0))
   competition.saveFixture(owner,upcoming)
   assertEquals("SCHEDULED",MatchSheetRepository.decode(db.userDataDao().read(owner,MatchSheetRepository.key(upcoming.id))).phase)
   sheets.update(owner,upcoming.id) { it.copy(phase="LIVE",minute=32) }
   val backup=BackupRepository(db);val document=backup.decode(backup.export(owner))
   assertEquals(21,document.sheets.size)
   assertTrue(document.sheets.all { it.sheet.photos.isEmpty() })
   val idsBefore=data.fixtures.map { it.id }.toSet()
   backup.restore(owner,document)
   val copied=CompetitionRepository(db).snapshot.first { it.fixtures.size==42 && it.sheets.size==42 }
   val imported=copied.fixtures.filter { it.id !in idsBefore }
   imported.forEach { f -> MatchSheetRepository.validate(copied.sheets.getValue(f.id),f,copied.players) }
   assertEquals(document.sheets.sumOf { it.sheet.substitutions.size },imported.sumOf { copied.sheets.getValue(it.id).substitutions.size })
   scope.cancel();db.close();db=open()
   val reopened=CompetitionRepository(db).snapshot.first { it.fixtures.size==42 && it.sheets.size==42 }
   assertEquals("LIVE",reopened.sheets.getValue(upcoming.id).phase)
   assertEquals(32,reopened.sheets.getValue(upcoming.id).minute)
   assertEquals(1,reopened.sheets.getValue(fixture.id).substitutions.size)
  } finally { scope.cancel();db.close();context.deleteDatabase(name) }
 }
}
