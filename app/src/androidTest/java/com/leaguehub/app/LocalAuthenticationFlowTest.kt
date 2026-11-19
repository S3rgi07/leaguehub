package com.leaguehub.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class LocalAuthenticationFlowTest {
    @get:Rule val compose=createEmptyComposeRule()
    private fun waitText(text: String) { compose.waitUntil(15_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() } }
    private fun input(index: Int,value: String) { compose.onAllNodes(hasSetTextAction())[index].performScrollTo().performTextReplacement(value) }
    private fun click(text: String) { compose.onNodeWithText(text).performScrollTo().performClick() }
    @Test fun registrationPasswordChangeLogoutLoginAndActivityRecreationWork() {
        val app=InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as LeagueHubApplication
        val sessions=app.userSessionRepository
        val email="auth-${UUID.randomUUID()}@prueba.com"
        var testId: String?=null
        var scenario: ActivityScenario<MainActivity>?=null
        runBlocking { sessions.restoreCompleted.first { it } }
        val previous=sessions.session.value
        val previousStored=runBlocking { app.database.userSessionDao().observeSession().first() }
        try {
            sessions.logout()
            runBlocking { withTimeout(5_000) { while(app.database.userSessionDao().observeSession().first()!=null) delay(20) } }
            scenario=ActivityScenario.launch(MainActivity::class.java)
            waitText("Comenzar");compose.onNodeWithText("Comenzar").performClick()
            waitText("Tu perfil deportivo")
            input(0,"Prueba Teléfono");input(1,email);input(2,"Prueba123")
            val terms=compose.onNode(isToggleable()).performScrollTo()
            if(terms.fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.ToggleableState]==androidx.compose.ui.state.ToggleableState.Off) terms.performClick()
            click("Continuar")
            waitText("Elige tu experiencia")
            click("Organizador");click("Continuar como organizador")
            compose.waitUntil(15_000) { compose.onAllNodesWithTag("tab-PROFILE").fetchSemanticsNodes().isNotEmpty() }
            testId=sessions.session.value.userId
            compose.onNodeWithTag("tab-PROFILE").performClick()
            compose.onNodeWithText("Prueba Teléfono").assertIsDisplayed()
            compose.onNodeWithTag("local-list").performScrollToNode(hasText("Cambiar contraseña"))
            compose.onNodeWithText("Cambiar contraseña").performClick()
            compose.onNodeWithTag("field-current").performScrollTo().performTextReplacement("Prueba123")
            compose.onNodeWithTag("field-new").performScrollTo().performTextReplacement("NuevaClave123")
            compose.onNodeWithTag("field-confirm").performScrollTo().performTextReplacement("NuevaClave123")
            compose.onNodeWithTag("form-save").performClick()
            compose.waitUntil(15_000) { compose.onAllNodesWithTag("form-save").fetchSemanticsNodes().isEmpty() }
            compose.onNodeWithTag("local-list").performScrollToNode(hasText("Cerrar sesión"))
            compose.onNodeWithText("Cerrar sesión").performClick()
            compose.onNodeWithText("Salir").performClick()
            waitText("Qué bueno verte")
            input(0,email);input(1,"NuevaClave123");click("Ingresar")
            compose.waitUntil(15_000) { compose.onAllNodesWithTag("tab-PROFILE").fetchSemanticsNodes().isNotEmpty() }
            assertEquals(testId,sessions.session.value.userId)
            scenario.recreate()
            compose.waitUntil(15_000) { compose.onAllNodesWithTag("tab-PROFILE").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("tab-PROFILE").performClick()
            compose.onNodeWithText(email).assertIsDisplayed()
        } finally {
            scenario?.close()
            runBlocking {
                sessions.replacePersistedSession(previous) {
                    if(previousStored==null) app.database.userSessionDao().clear() else app.database.userSessionDao().upsert(previousStored)
                    testId?.let { app.database.openHelper.writableDatabase.execSQL("DELETE FROM user_accounts WHERE userId = ?",arrayOf(it)) }
                }
            }
        }
    }
}
