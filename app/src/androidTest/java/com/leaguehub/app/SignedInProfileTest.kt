package com.leaguehub.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.repository.AuthRepository
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.feature.profile.ProfileScreen
import com.leaguehub.app.feature.profile.ProfileViewModel
import com.leaguehub.app.ui.theme.LeagueHubTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test

class SignedInProfileTest {
    @get:Rule val compose = createComposeRule()

    @Test fun myProfileFollowsTheSignedInAccountWithoutShowingSofiaStats() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, LeagueHubDatabase::class.java).build()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val sessions = UserSessionRepository(database.userSessionDao(), scope, database.userAccountDao())
            val auth = AuthRepository(database, sessions)
            runBlocking {
                sessions.restoreCompleted.first { it }
                auth.register("María López", "maria-profile@example.com", "Prueba123")
            }
            val profile = ProfileViewModel(null, sessions)
            compose.setContent {
                val state by profile.state.collectAsStateWithLifecycle()
                LeagueHubTheme { ProfileScreen(state.player, state.team, {}, {}, {}) }
            }
            compose.onNodeWithText("María López").assertIsDisplayed()
            compose.onNodeWithText("Sin equipo · Sin posición").assertIsDisplayed()
            compose.onNodeWithText("Sofía Méndez").assertDoesNotExist()
            compose.onNodeWithText("12").assertDoesNotExist()
            runBlocking { auth.register("Carlos Ruiz", "carlos-profile@example.com", "Prueba123") }
            compose.waitUntil(10_000) { compose.onAllNodesWithText("Carlos Ruiz").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Carlos Ruiz").assertIsDisplayed()
            compose.onNodeWithText("María López").assertDoesNotExist()
            runBlocking { auth.login("maria-profile@example.com", "Prueba123") }
            compose.waitUntil(10_000) { compose.onAllNodesWithText("María López").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("María López").assertIsDisplayed()
        } finally { scope.cancel(); database.close() }
    }
}