package com.leaguehub.app

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.ui.theme.LeagueHubTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

class BackupFlowTest {
    @get:Rule val compose=createComposeRule()
    private fun click(text: String) { compose.onNodeWithTag("local-list").performScrollToNode(hasText(text));compose.onNodeWithText(text).performClick() }
    @Test fun profileExportsPreviewsCancelsAndImportsTheSelectedFile() {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val db=Room.inMemoryDatabaseBuilder(context,LeagueHubDatabase::class.java).build()
        val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
        val file=File(context.cacheDir,"backup-test-${UUID.randomUUID()}.json")
        try {
            val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao())
            val auth=AuthRepository(db,sessions)
            val repo=CompetitionRepository(db)
            runBlocking {
                sessions.restoreCompleted.first { it };auth.register("Dueño","file@prueba.com","Prueba123")
                repo.saveLeague(sessions.session.value.userId!!,LeagueEntity("l",sessions.session.value.userId!!,"Liga de archivo","2026"))
            }
            sessions.selectRole(SessionRole.ORGANIZER)
            val local=LocalHubViewModel(repo,sessions,auth,db.userAccountDao())
            val backup=BackupViewModel(BackupRepository(db),sessions,context.contentResolver)
            compose.setContent {
                val state by local.state.collectAsStateWithLifecycle()
                val data by backup.state.collectAsStateWithLifecycle()
                LeagueHubTheme {
                    LocalHubScreen(state,local,backupBusy=data.busy,onExport={backup.export(Uri.fromFile(file))},onImport={backup.inspect(Uri.fromFile(file))})
                    BackupDialogs(data,backup::confirm,backup::dismiss)
                }
            }
            compose.waitUntil(10_000) { !local.state.value.loading }
            compose.onNodeWithTag("tab-PROFILE").performClick()
            click("Exportar respaldo")
            compose.waitUntil(10_000) { !backup.state.value.busy && backup.state.value.message!=null }
            assertTrue(file.exists());assertFalse(file.readText().contains("Prueba123"))
            compose.onNodeWithText("Aceptar").performClick()
            click("Importar respaldo")
            compose.waitUntil(10_000) { backup.state.value.preview!=null && !backup.state.value.busy }
            compose.onNodeWithText("Importar copias").assertIsDisplayed()
            compose.onNodeWithText("Cancelar").performClick()
            assertEquals(1,local.state.value.data.leagues.size)
            click("Importar respaldo")
            compose.waitUntil(10_000) { backup.state.value.preview!=null && !backup.state.value.busy }
            compose.onNodeWithText("Importar copias").performClick()
            compose.waitUntil(10_000) { local.state.value.data.leagues.size==2 && !backup.state.value.busy }
            compose.onNodeWithText("Aceptar").performClick()
            val before=backup.state.value
            backup.export(null);backup.inspect(null)
            assertEquals(before,backup.state.value)
            file.writeText("{archivo inválido")
            click("Importar respaldo")
            compose.waitUntil(10_000) { backup.state.value.error!=null && !backup.state.value.busy }
            compose.onNodeWithText("No se pudo completar").assertIsDisplayed()
            assertEquals(2,local.state.value.data.leagues.size)
        } finally { scope.cancel();db.close();file.delete() }
    }
}
