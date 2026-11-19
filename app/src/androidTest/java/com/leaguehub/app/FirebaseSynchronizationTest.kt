package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.leaguehub.app.data.cloud.*
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.util.UUID

/** Real Firebase SDK + Auth/Firestore emulators, two independent local databases and SDK sessions. */
class FirebaseSynchronizationTest {
 private val context=ApplicationProvider.getApplicationContext<Context>()
 private suspend fun until(condition:suspend ()->Boolean) { withTimeout(45_000) { while(!condition()) delay(150) } }
 private fun gateway(name:String):Pair<FirebaseApp,FirebaseGateway> {
  val options=FirebaseOptions.Builder().setProjectId("demo-leaguehub").setApplicationId("1:123456789:android:abcdef123456").setApiKey("fake-api-key").build()
  val app=FirebaseApp.initializeApp(context,options,name)
  val auth=FirebaseAuth.getInstance(app).apply { useEmulator("10.0.2.2",9099) }
  val store=FirebaseFirestore.getInstance(app).apply { firestoreSettings=FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();useEmulator("10.0.2.2",8080) }
  return app to FirebaseGateway(auth,store)
 }
 @Test fun twoPhonesMigrateSyncReconnectResolveConflictsAndKeepDeletionTombstones() = runBlocking {
  assumeTrue("Start Firebase emulators and pass firebaseEmulator=true",InstrumentationRegistry.getArguments().getString("firebaseEmulator")=="true")
  val unique=UUID.randomUUID().toString();val dbName="cloud-b-$unique.db"
  fun openB()=Room.databaseBuilder(context,LeagueHubDatabase::class.java,dbName).addCallback(LeagueHubDatabase.SYNC_CALLBACK).build()
  val a=Room.inMemoryDatabaseBuilder(context,LeagueHubDatabase::class.java).addCallback(LeagueHubDatabase.SYNC_CALLBACK).build()
  var b=openB()
  val scopeA=CoroutineScope(SupervisorJob()+Dispatchers.IO);var scopeB=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  val (appA,remoteA)=gateway("phone-a-$unique");val (appB,remoteB)=gateway("phone-b-$unique")
  try {
   val email="nube-$unique@prueba.com";val password="NubePrueba2026!"
   val sessionsA=UserSessionRepository(a.userSessionDao(),scopeA,a.userAccountDao());sessionsA.restoreCompleted.first { it }
   AuthRepository(a,sessionsA).register("Ana de nube",email,password)
   val localOwnerA=sessionsA.session.value.userId!!
   sessionsA.selectRole(SessionRole.ORGANIZER)
   val authA=AuthRepository(a,sessionsA,remoteA);authA.connectCurrent(password)
   assertNotEquals(localOwnerA,remoteA.currentUid)
   val league="cloud-$unique";val club="home-$unique";val away="away-$unique";val player="player-$unique";val playerAway="away-player-$unique";val fixture="match-$unique"
   val repoA=CompetitionRepository(a)
   repoA.saveLeague(localOwnerA,LeagueEntity(league,localOwnerA,"Liga sincronizada","2026"))
   repoA.saveClub(localOwnerA,ClubEntity(club,league,"Atlas"));repoA.saveClub(localOwnerA,ClubEntity(away,league,"Bravos"))
   repoA.savePlayer(localOwnerA,PlayerEntity(player,club,"Ana de nube",10,"Delantera",localOwnerA));repoA.savePlayer(localOwnerA,PlayerEntity(playerAway,away,"Otro jugador",9,"Delantero"))
   repoA.saveFixture(localOwnerA,FixtureEntity(fixture,league,club,away,"2026-11-19T18:00","Central",1,2,1))
   repoA.saveEvent(localOwnerA,MatchEventEntity("goal-$unique",fixture,player,"GOAL",10))
   repoA.saveEvent(localOwnerA,MatchEventEntity("appearance-$unique",fixture,player,"APPEARANCE",90,90,8.5))
   MatchSheetRepository(a).update(localOwnerA,fixture) { it.copy(teams=listOf(TeamSheet(club,"2-2",listOf(player)))) }
   val engineA=CloudSyncEngine(a,sessionsA,remoteA,remoteA,scopeA);engineA.start()
   until { (a.syncDao().league(league)?.revision ?: 0)>0 && a.syncDao().league(league)?.let { it.localVersion==it.syncedVersion }==true }
   var sessionsB=UserSessionRepository(b.userSessionDao(),scopeB,b.userAccountDao());sessionsB.restoreCompleted.first { it }
   AuthRepository(b,sessionsB,remoteB).login(email,password)
   val localOwnerB=sessionsB.session.value.userId!!;sessionsB.selectRole(SessionRole.ORGANIZER)
   assertEquals(remoteA.currentUid,localOwnerB)
   var engineB=CloudSyncEngine(b,sessionsB,remoteB,remoteB,scopeB);engineB.start()
   until { b.competitionDao().player(player)?.accountId==localOwnerB }
   assertEquals(localOwnerB,b.competitionDao().league(league)?.ownerId)
   assertEquals(1,CompetitionRepository(b).snapshot.first { it.events.any { e->e.playerId==player } }.totals(player).goals)
   assertTrue(MatchSheetRepository.decode(b.userDataDao().read(localOwnerB,MatchSheetRepository.key(fixture))).teams.isNotEmpty())
   val payload=remoteA.firestore.collection("leagues").document(league).get(Source.SERVER).awaitCloud().getString("payload")!!
   assertFalse(payload.contains("passwordHash"));assertFalse(payload.contains("passwordSalt"));assertFalse(payload.contains(email))
   repoA.saveClub(localOwnerA,ClubEntity(club,league,"Atlas actualizado"));until { b.competitionDao().club(club)?.name=="Atlas actualizado" }
   remoteB.firestore.disableNetwork().awaitCloud();delay(500)
   CompetitionRepository(b).saveClub(localOwnerB,ClubEntity(club,league,"Edición B sin internet"))
   repoA.saveClub(localOwnerA,ClubEntity(club,league,"Edición A online"))
   until { a.syncDao().league(league)?.let { it.localVersion==it.syncedVersion }==true }
   scopeB.cancel();b.close();b=openB();scopeB=CoroutineScope(SupervisorJob()+Dispatchers.IO)
   assertTrue(b.syncDao().league(league)!!.localVersion>b.syncDao().league(league)!!.syncedVersion)
   sessionsB=UserSessionRepository(b.userSessionDao(),scopeB,b.userAccountDao());sessionsB.restoreCompleted.first { it }
   engineB=CloudSyncEngine(b,sessionsB,remoteB,remoteB,scopeB);engineB.start()
   remoteB.firestore.enableNetwork().awaitCloud()
   until { b.syncDao().league(league)?.conflictPayload!=null }
   assertEquals("Edición B sin internet",b.competitionDao().club(club)?.name)
   engineB.resolve(league,keepLocal=false)
   assertEquals("Edición A online",b.competitionDao().club(club)?.name)
   val recovery=BackupRepository(b).decode(engineB.exportRecoveries())
   assertEquals("Edición B sin internet",recovery.clubs.first { it.id==club }.name)
   remoteB.firestore.disableNetwork().awaitCloud();delay(500)
   val game=b.competitionDao().fixture(fixture)!!
   CompetitionRepository(b).saveFixture(localOwnerB,game.copy(venue="Cambio offline conservado"))
   remoteB.firestore.enableNetwork().awaitCloud()
   until { a.competitionDao().fixture(fixture)?.venue=="Cambio offline conservado" }
   a.withTransaction {
    repoA.delete(localOwnerA,"fixture",fixture)
    repoA.delete(localOwnerA,"player",player);repoA.delete(localOwnerA,"player",playerAway)
    repoA.delete(localOwnerA,"club",club);repoA.delete(localOwnerA,"club",away);repoA.delete(localOwnerA,"league",league)
   }
   until { b.competitionDao().league(league)==null && b.syncDao().league(league)?.deleted==true }
   assertTrue(remoteA.firestore.collection("leagues").document(league).get(Source.SERVER).awaitCloud().getBoolean("deleted")!!)
   sessionsB.logoutAndWait();until { remoteB.currentUid==null }
  } finally {
   scopeA.cancel();scopeB.cancel();a.close();b.close();context.deleteDatabase(dbName)
   remoteA.firestore.terminate().awaitCloud();remoteB.firestore.terminate().awaitCloud();appA.delete();appB.delete()
  }
 }
}
