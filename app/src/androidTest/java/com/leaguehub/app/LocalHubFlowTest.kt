package com.leaguehub.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.ui.theme.LeagueHubTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LocalHubFlowTest {
    @get:Rule val compose=createComposeRule()
    private lateinit var vm: LocalHubViewModel
    private fun waitSaved() { compose.waitUntil(10_000) { !vm.state.value.busy && vm.state.value.editor==null }; compose.waitForIdle() }
    private fun field(key: String,value: String) { compose.onNodeWithTag("field-$key").performScrollTo().performTextReplacement(value) }
    private fun tab(section: String) { compose.onNodeWithTag("local-list").performScrollToIndex(0);compose.onNodeWithTag("tab-$section").performClick() }
    private fun click(text: String) { compose.onNodeWithTag("local-list").performScrollToNode(hasText(text));compose.onNodeWithText(text).performClick() }
    private fun save() { compose.onNodeWithTag("form-save").performClick();waitSaved() }

    @Test fun createsLeagueTeamsPlayerMatchAndResultThroughTheForms() {
        val db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),LeagueHubDatabase::class.java).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao())
            val auth=AuthRepository(db,sessions)
            runBlocking { sessions.restoreCompleted.first { it };auth.register("Ana Local","ana-ui@prueba.com","Prueba123") }
            sessions.selectRole(SessionRole.ORGANIZER)
            vm=LocalHubViewModel(CompetitionRepository(db),sessions,auth,db.userAccountDao())
            compose.setContent { val s by vm.state.collectAsStateWithLifecycle();LeagueHubTheme { LocalHubScreen(s,vm) } }
            compose.waitUntil(10_000) { !vm.state.value.loading }
            tab("LEAGUES");click("Crear liga")
            compose.onNodeWithTag("form-save").performClick()
            compose.waitUntil(5_000) { vm.state.value.error!=null }
            field("name","Liga del teléfono");field("season","Apertura 2026");save()
            compose.waitUntil(5_000) { vm.state.value.data.leagues.size==1 }
            tab("CLUBS");click("Crear equipo");field("name","Atlas");save()
            click("Crear equipo");field("name","Bravos");save()
            compose.waitUntil(5_000) { vm.state.value.clubs.size==2 }
            tab("PLAYERS");click("Crear jugador")
            field("name","Ana Local");field("number","10");field("position","Delantera");field("email","ana-ui@prueba.com");save()
            compose.waitUntil(5_000) { vm.state.value.players.size==1 }
            tab("MATCHES");click("Crear partido")
            field("date","2026-11-20");field("time","16:30");field("venue","Cancha central");save()
            compose.waitUntil(5_000) { vm.state.value.fixtures.size==1 }
            click("Registrar resultado");field("homeScore","1");field("awayScore","0");save()
            click("Ver eventos");click("Agregar evento")
            field("minute","15");save()
            compose.waitUntil(5_000) { vm.state.value.data.events.size==1 }
            tab("TABLE")
            compose.onNodeWithTag("local-list").performScrollToNode(hasText("1. Atlas"))
            compose.onNodeWithText("1. Atlas").assertIsDisplayed()
            assertEquals(3,vm.state.value.data.table(vm.state.value.leagueId!!).first().points)
            tab("PROFILE")
            compose.onNodeWithText("Ana Local").assertIsDisplayed()
            compose.onNodeWithText("1 goles · 0 asistencias · 1 partidos").assertIsDisplayed()
            click("Editar mi nombre");field("name","Ana Actualizada");save()
            compose.onNodeWithText("Ana Actualizada").assertIsDisplayed()
            tab("MATCHES");click("Ver eventos")
            val event=vm.state.value.data.events.single()
            compose.runOnIdle { vm.delete("event",event.id,"Gol") }
            compose.onNodeWithText("Eliminar",useUnmergedTree=true).performClick()
            compose.waitUntil(5_000) { vm.state.value.data.events.isEmpty() }
            click("Corregir resultado");field("homeScore","0");field("awayScore","2");save()
            compose.waitUntil(5_000) { vm.state.value.data.table(vm.state.value.leagueId!!).first().club.name=="Bravos" }
            assertEquals(0,vm.state.value.data.totals(vm.state.value.myPlayer!!.id).goals)
        } finally { scope.cancel();db.close() }
    }
    @Test fun editDeleteDependenciesAndReadOnlyModeAreHandledInTheInterface() {
        val db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),LeagueHubDatabase::class.java).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao())
            val auth=AuthRepository(db,sessions)
            val repo=CompetitionRepository(db)
            runBlocking {
                sessions.restoreCompleted.first { it };auth.register("Dueño Local","owner-ui@prueba.com","Prueba123")
                val owner=sessions.session.value.userId!!
                repo.saveLeague(owner,com.leaguehub.app.data.local.LeagueEntity("l",owner,"Liga editable","2026"))
                repo.saveClub(owner,com.leaguehub.app.data.local.ClubEntity("a","l","Atlas"))
                repo.savePlayer(owner,com.leaguehub.app.data.local.PlayerEntity("p","a","Ana",10,"Delantera"))
            }
            sessions.selectRole(SessionRole.ORGANIZER)
            vm=LocalHubViewModel(repo,sessions,auth,db.userAccountDao())
            compose.setContent { val s by vm.state.collectAsStateWithLifecycle();LeagueHubTheme { LocalHubScreen(s,vm) } }
            compose.waitUntil(10_000) { !vm.state.value.loading }
            tab("LEAGUES");click("Editar liga");field("name","Liga corregida");save()
            compose.waitUntil(5_000) { vm.state.value.league?.name=="Liga corregida" }
            tab("CLUBS");click("Eliminar")
            compose.onAllNodesWithText("Eliminar").onLast().performClick()
            compose.waitUntil(5_000) { vm.state.value.error!=null }
            assertEquals(1,vm.state.value.clubs.size)
            compose.onNodeWithText("Cancelar").performClick()
            click("Editar");field("name","Atlas corregido");save()
            compose.waitUntil(5_000) { vm.state.value.clubs.single().name=="Atlas corregido" }
            tab("PLAYERS");click("Editar");field("name","Ana corregida");field("number","9");save()
            compose.waitUntil(5_000) { vm.state.value.players.single().number==9 }
            tab("PROFILE");click("Cambiar a jugador")
            compose.waitUntil(5_000) { vm.state.value.session.role==SessionRole.PLAYER }
            tab("CLUBS")
            compose.onNodeWithText("Crear equipo").assertDoesNotExist()
            compose.onNodeWithText("Editar").assertDoesNotExist()
            tab("PROFILE");click("Cambiar a organizador")
            compose.waitUntil(5_000) { vm.state.value.session.role==SessionRole.ORGANIZER }
            tab("PLAYERS");click("Eliminar");compose.onAllNodesWithText("Eliminar").onLast().performClick()
            compose.waitUntil(5_000) { vm.state.value.players.isEmpty() }
            tab("CLUBS");click("Eliminar");compose.onAllNodesWithText("Eliminar").onLast().performClick()
            compose.waitUntil(5_000) { vm.state.value.clubs.isEmpty() }
            tab("LEAGUES");click("Eliminar liga");compose.onAllNodesWithText("Eliminar").onLast().performClick()
            compose.waitUntil(5_000) { vm.state.value.data.leagues.isEmpty() }
        } finally { scope.cancel();db.close() }
    }}
