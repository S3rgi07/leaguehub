package com.leaguehub.app

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.local.LocalHubViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.LocalDate

/** Exercises restored screens against Room; images are visual-review artifacts, not production data. */
class InterfaceRecoveryTest {
 @get:Rule val compose=createComposeRule()
 private val context=ApplicationProvider.getApplicationContext<Context>()
 private fun capture(name:String) {
  compose.waitForIdle()
  val directory=(androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { File(it,"interface-recovery") } ?: File(context.getExternalFilesDir(null),"interface-recovery")).apply { mkdirs() }
  File(directory,"$name.png").outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG,100,it) }
 }
 @Test fun restoredScreensDisplayAccountStatisticsAndFilterRealFixtures() {
  val db=Room.inMemoryDatabaseBuilder(context,LeagueHubDatabase::class.java).build()
  val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  try {
   val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao())
   val auth=AuthRepository(db,sessions)
   val repo=CompetitionRepository(db)
   val today=LocalDate.now()
   runBlocking {
    sessions.restoreCompleted.first { it };auth.register("Ana Martínez","visual@prueba.com","Prueba123")
    val owner=sessions.session.value.userId!!
    repo.saveLeague(owner,LeagueEntity("l",owner,"Liga Metropolitana","Apertura 2026"))
    repo.saveClub(owner,ClubEntity("a","l","Atlas"));repo.saveClub(owner,ClubEntity("b","l","Bravos"));repo.saveClub(owner,ClubEntity("c","l","Cóndores"))
    repo.savePlayer(owner,PlayerEntity("p","a","Ana Martínez",10,"Delantera",owner))
    repeat(5) { n ->
     val id="f$n"
     repo.saveFixture(owner,FixtureEntity(id,"l","a","b","${today.minusDays((5-n).toLong())}T16:30","Cancha Central",n+1,2,1))
     repo.saveEvent(owner,MatchEventEntity("goal$n",id,"p","GOAL",20))
     repo.saveEvent(owner,MatchEventEntity("app$n",id,"p","APPEARANCE",90,90,8.0+n*.2))
    }
    repo.saveFixture(owner,FixtureEntity("next","l","a","c","${today}T18:00","Estadio Municipal",6))
   }
   sessions.selectRole(SessionRole.ORGANIZER)
   val store=LocalHubViewModel(repo,sessions,auth,db.userAccountDao())
   compose.setContent { LocalTestHost(store,sessions,db) }
   compose.waitUntil(10_000) { !store.state.value.loading }
   compose.onNodeWithTag("local-list").performScrollToIndex(0)
   capture("inicio")
   compose.onNodeWithTag("tab-PROFILE").performClick()
   compose.onNodeWithText("Ana Martínez").assertIsDisplayed()
   compose.onNodeWithText("Goles").assertIsDisplayed()
   compose.onNodeWithText("450").assertIsDisplayed()
   compose.onNodeWithText("Sofía Méndez").assertDoesNotExist()
   capture("perfil")
   compose.onNodeWithTag("tab-TABLE").performClick()
   compose.onNodeWithText("Atlas").assertIsDisplayed()
   compose.onNodeWithText("15").assertIsDisplayed()
   capture("tabla")
   compose.onNodeWithText("Visitante").performClick()
   compose.onNodeWithText("15").assertDoesNotExist()
   compose.onNodeWithText("General").performClick()
   compose.onNodeWithText("15").assertIsDisplayed()
   compose.onNodeWithTag("tab-MATCHES").performClick()
   capture("calendario")
   compose.onNodeWithContentDescription("$today, hay partido").performClick()
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Estadio Municipal"))
   compose.onNodeWithText("Estadio Municipal").assertIsDisplayed()
   compose.onNodeWithText("Cancha Central").assertDoesNotExist()
   compose.onNodeWithTag("local-list").performScrollToIndex(0)
   compose.onNodeWithContentDescription("Mes anterior").performClick()
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("No hay partidos para esta fecha."))
   compose.onNodeWithText("No hay partidos para esta fecha.").assertIsDisplayed()
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Ver todos los partidos"))
   compose.onNodeWithText("Ver todos los partidos").performClick()
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Cancha Central"))
   compose.onAllNodesWithText("Cancha Central").onFirst().assertIsDisplayed()
  } finally { scope.cancel();db.close() }
 }
}
