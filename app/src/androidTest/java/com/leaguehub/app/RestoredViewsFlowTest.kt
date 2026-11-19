package com.leaguehub.app
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.navigation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
class RestoredViewsFlowTest {
 @get:Rule val compose=createComposeRule()
 private val context=ApplicationProvider.getApplicationContext<Context>()
 private fun click(text:String) { compose.onNodeWithTag("local-list").performScrollToNode(hasText(text));compose.onNodeWithText(text).performClick() }
 private fun capture(name:String) { compose.waitForIdle();val dir=(androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { File(it,"restored-views") } ?: File(context.getExternalFilesDir(null),"restored-views")).apply { mkdirs() };File(dir,"$name.png").outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG,100,it) } }
 @Test fun matchTabsLineupsSettingsAlertsAndOriginalDetailViewsAreReachable() {
  val db=Room.inMemoryDatabaseBuilder(context,LeagueHubDatabase::class.java).build();val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  try {
   val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());val auth=AuthRepository(db,sessions)
   runBlocking { sessions.restoreCompleted.first { it };auth.login(DemoAccount.EMAIL,DemoAccount.PASSWORD) }
   val store=LocalHubViewModel(CompetitionRepository(db),sessions,auth,db.userAccountDao())
   lateinit var nav:NavHostController
   compose.setContent { LocalTestHost(store,sessions,db,onNavReady={nav=it}) }
   compose.waitUntil(15_000) { store.state.value.data.sheets.size==21 }
   val match=store.state.value.fixtures.first { it.homeScore!=null }
   compose.runOnIdle { nav.navigate(MatchCenterDestination(match.id)) }
   compose.waitUntil(10_000) { compose.onAllNodesWithText("Cronología").fetchSemanticsNodes().isNotEmpty() }
   capture("match-center")
   click("Alineación")
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Jugadores en cancha"))
   compose.onNodeWithText("Jugadores en cancha").assertIsDisplayed()
   capture("alineaciones")
   click("Editar alineación")
   compose.onNodeWithText("Titulares y formación").assertIsDisplayed()
   compose.onNodeWithText("Guardar alineación").performClick()
   compose.waitUntil(10_000) { compose.onAllNodesWithText("Titulares y formación").fetchSemanticsNodes().isEmpty() }
   compose.onNodeWithTag("local-list").performScrollToIndex(0);click("Estadísticas")
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Goles registrados"));capture("estadisticas-partido")
   compose.onNodeWithTag("local-list").performScrollToIndex(0);click("Galería")
   compose.onNodeWithTag("local-list").performScrollToNode(hasText("Galería del partido"));capture("galeria")
   compose.onNodeWithTag("tab-PROFILE").performClick()
   compose.onNodeWithContentDescription("Configuración").performClick()
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<SettingsDestination>()) }
   capture("configuracion")
   click("Privacidad y datos");compose.onNodeWithText("Entendido").performClick()
   click("Notificaciones")
   compose.runOnIdle { assertTrue(nav.currentBackStackEntry!!.destination.hasRoute<NotificationsDestination>()) }
   capture("notificaciones")
   click("Marcar todas leídas")
   compose.waitUntil(10_000) { runBlocking { db.userDataDao().read(DemoAccount.USER_ID,"read_alerts")!=null } }
   compose.runOnIdle { nav.navigate(LeagueDetailDestination(DemoAccount.LEAGUE_ID)) }
   compose.onNodeWithText("Liga Estelar · Demo").assertIsDisplayed();capture("detalle-liga")
   compose.runOnIdle { nav.navigate(TeamDetailDestination("demo-club-0")) }
   compose.onNodeWithText("Falcons").assertIsDisplayed();capture("detalle-equipo")
   compose.onNode(hasScrollAction()).performScrollToNode(hasText("Ver plantilla completa"));compose.onNodeWithText("Ver plantilla completa").performClick()
   compose.onNode(hasScrollAction()).performScrollToNode(hasText("Ana Torres"))
   compose.onNodeWithText("Ana Torres").performClick()
   compose.onNodeWithText("Cerrar ficha").performClick()
   compose.runOnIdle { nav.navigate(MatchDetailDestination(match.id)) }
   compose.onNodeWithText("Detalle del partido").assertIsDisplayed();capture("detalle-partido")
   compose.runOnIdle { nav.navigate(ExploreDestination) }
   compose.onNodeWithText("Explorar ligas").assertIsDisplayed()
   compose.runOnIdle { nav.navigate(OrganizerDashboardDestination) }
   compose.onNodeWithText("Panel organizador").assertIsDisplayed();capture("panel-organizador")
  } finally { scope.cancel();db.close() }
 }
}
