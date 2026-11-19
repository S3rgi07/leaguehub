package com.leaguehub.app
import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.remote.*
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.navigation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
class LocalMviNavigationTest {
 @get:Rule val compose=createComposeRule()
 private fun click(text:String) { compose.onNodeWithTag("local-list").performScrollToNode(hasText(text));compose.onNodeWithText(text).performClick() }
 private fun tab(name:String) { if(name in listOf("LEAGUES","CLUBS","PLAYERS")) compose.onNodeWithTag("local-list").performScrollToNode(hasTestTag("tab-$name"))
  compose.onNodeWithTag("tab-$name").performClick() }
 @Test fun typedDetailsBackNavigationAndRemoteRetryKeepLocalDataUsableOffline() {
  val db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),LeagueHubDatabase::class.java).build()
  val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  try {
   val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());val auth=AuthRepository(db,sessions);val repo=CompetitionRepository(db)
   runBlocking {
    sessions.restoreCompleted.first { it };auth.register("Dueño MVI","mvi@prueba.com","Prueba123")
    val owner=sessions.session.value.userId!!
    repo.saveLeague(owner,LeagueEntity("l",owner,"Liga MVI","2026"));repo.saveClub(owner,ClubEntity("a","l","Atlas"));repo.saveClub(owner,ClubEntity("b","l","Bravos"))
    repo.savePlayer(owner,PlayerEntity("p","a","Ana",10,"Delantera"));repo.saveFixture(owner,FixtureEntity("f","l","a","b","2026-11-20T16:00","Central",1,1,0))
   }
   sessions.selectRole(SessionRole.ORGANIZER)
   val store=LocalHubViewModel(repo,sessions,auth,db.userAccountDao())
   val calls=AtomicInteger()
   val community=CommunityRepository(object:LeagueHubApi { override suspend fun getCommunityAnnouncement():CommunityAnnouncementDto {
    if(calls.incrementAndGet()==1) throw IOException("Sin conexión de prueba")
    return CommunityAnnouncementDto(1,"Conexión verificada","contenido")
   } })
   lateinit var nav:NavHostController
   compose.setContent { LocalTestHost(store,sessions,db,community) { nav=it } }
   compose.waitUntil(10_000) { !store.state.value.loading };compose.onNodeWithTag("local-list").performScrollToNode(hasTestTag("network-retry"));compose.waitUntil(10_000) { compose.onAllNodesWithText("Reintentar conexión").fetchSemanticsNodes().isNotEmpty() }
   tab("LEAGUES");click("Abrir Liga MVI");click("Ver plantilla de Atlas")
   compose.runOnIdle { assertEquals("a",nav.currentBackStackEntry!!.toRoute<LocalPlayersDestination>().clubId) }
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("#10 Ana"));compose.onNodeWithText("#10 Ana").assertIsDisplayed()
   compose.runOnIdle { nav.popBackStack() }
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<LocalClubsDestination>()) }
   tab("MATCHES");click("Ver eventos")
   compose.runOnIdle { assertEquals("f",nav.currentBackStackEntry!!.toRoute<LocalEventsDestination>().matchId) }
   click("Cerrar eventos")
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<LocalMatchesDestination>()) }
   tab("PROFILE");click("Respaldos")
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<LocalBackupDestination>()) }
   click("Volver")
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<LocalProfileDestination>()) }
   tab("TABLE");compose.onNodeWithTag("local-list").performScrollToNode(hasText("Atlas"));compose.onNodeWithText("Atlas").assertIsDisplayed()
   assertEquals(1,calls.get())
   tab("HOME");click("Reintentar conexión")
   compose.waitUntil(10_000) { compose.onAllNodesWithText("Conexión verificada").fetchSemanticsNodes().isNotEmpty() }
   assertEquals(2,calls.get())
  } finally { scope.cancel();db.close() }
 }
}
