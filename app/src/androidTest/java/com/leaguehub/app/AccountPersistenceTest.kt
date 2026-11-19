package com.leaguehub.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.repository.AuthRepository
import com.leaguehub.app.data.repository.UserScopedState
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import com.leaguehub.app.feature.organizer.MatchWorkflowState
import com.leaguehub.app.feature.organizer.WorkflowSnapshot
import com.leaguehub.app.feature.profile.ProfileViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AccountPersistenceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private suspend fun awaitCondition(condition: suspend () -> Boolean) = withTimeout(10_000) {
        while (!condition()) delay(20)
    }
    private suspend fun rejected(block: suspend () -> Unit) {
        try { block(); fail("La operación debía rechazarse") } catch (_: IllegalArgumentException) { }
    }

    @Test fun accountsSurviveReopenAndRejectIncorrectCredentialsAndDuplicateEmail() = runBlocking {
        val name = "account-test-${UUID.randomUUID()}.db"
        var database = Room.databaseBuilder(context, LeagueHubDatabase::class.java, name).build()
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var sessions = UserSessionRepository(database.userSessionDao(), scope, database.userAccountDao())
            sessions.restoreCompleted.first { it }
            var auth = AuthRepository(database, sessions)
            auth.register("María López", "  MARIA@example.com ", "Prueba123")
            val mariaId = sessions.session.value.userId
            val account = database.userAccountDao().findByEmail("maria@example.com")!!
            assertNotEquals("Prueba123", account.passwordHash)
            assertTrue(account.passwordSalt.isNotBlank())
            sessions.selectRole(SessionRole.PLAYER)
            sessions.selectLeague("university_league_gt")
            sessions.completeOnboarding()
            awaitCondition { database.userAccountDao().findByEmail("maria@example.com")?.onboardingCompleted == true }
            rejected { auth.register("Otra María", "Maria@example.com", "Otra1234") }
            rejected { auth.login("maria@example.com", "incorrecta") }
            assertEquals(mariaId, sessions.session.value.userId)
            auth.register("Carlos Ruiz", "carlos@example.com", "Prueba123")
            assertNotEquals(mariaId, sessions.session.value.userId)
            assertNotEquals(account.passwordHash, database.userAccountDao().findByEmail("carlos@example.com")!!.passwordHash)
            sessions.logout()
            awaitCondition { database.userSessionDao().observeSession().first() == null }
            scope.cancel(); database.close()

            database = Room.databaseBuilder(context, LeagueHubDatabase::class.java, name).build()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            sessions = UserSessionRepository(database.userSessionDao(), scope, database.userAccountDao())
            sessions.restoreCompleted.first { it }
            assertFalse(sessions.session.value.isAuthenticated)
            auth = AuthRepository(database, sessions)
            auth.login("MARIA@example.com", "Prueba123")
            assertEquals(mariaId, sessions.session.value.userId)
            assertEquals("María López", sessions.session.value.userName)
            assertEquals(SessionRole.PLAYER, sessions.session.value.role)
            assertTrue(sessions.session.value.onboardingCompleted)
            assertEquals("university_league_gt", sessions.session.value.selectedLeagueId)
            val profile = ProfileViewModel(null, sessions).state.value
            assertEquals("María López", profile.player.name)
            assertEquals(0, profile.player.goals)
            assertNull(profile.team)
            assertEquals("Sofía Méndez", ProfileViewModel("sofia_mendez", sessions).state.value.player.name)
        } finally { scope.cancel(); database.close(); context.deleteDatabase(name) }
    }

    @Test fun userDataAndOrganizerChangesAreIsolatedAndRestored() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, LeagueHubDatabase::class.java).build()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val sessions = UserSessionRepository(database.userSessionDao(), scope, database.userAccountDao())
            sessions.restoreCompleted.first { it }
            val auth = AuthRepository(database, sessions)
            auth.register("Ana Pérez", "ana@example.com", "Prueba123")
            val anaId = sessions.session.value.userId!!
            fun textState() = UserScopedState(sessions, database.userDataDao(), scope, "appearance", { "Oscuro" }, { it }, { it })
            val appearance = textState()
            appearance.update { "Claro" }
            // Switching accounts immediately must not discard an already accepted edit.
            auth.register("Luis Pérez", "luis@example.com", "Prueba123")
            awaitCondition { database.userDataDao().read(anaId, "appearance") == "Claro" }
            awaitCondition { appearance.state.value == "Oscuro" }
            appearance.update { "Azul" }
            awaitCondition { appearance.state.value == "Azul" }
            auth.login("ana@example.com", "Prueba123")
            awaitCondition { appearance.state.value == "Claro" }
            assertEquals("Claro", textState().state.first { it == "Claro" })

            val gson = Gson()
            fun workflowStore() = UserScopedState(sessions, database.userDataDao(), scope, "workflow", { MatchWorkflowState() },
                { gson.toJson(WorkflowSnapshot.from(it)) }, { gson.fromJson(it, WorkflowSnapshot::class.java).toState() })
            val workflow = MatchWorkflowRepository(workflowStore())
            workflow.updateScore(4, 3)
            workflow.updateReferee("Árbitra de prueba")
            workflow.completeRoster("titans")
            workflow.editDetectedEvent("5")
            workflow.publish()
            withTimeout(10_000) { workflow.state.first { it.isPublished } }
            val restored = withTimeout(10_000) { workflowStore().state.first { it.isPublished } }
            assertEquals(4, restored.homeScore)
            assertEquals(3, restored.awayScore)
            assertEquals("Árbitra de prueba", restored.referee)
            assertTrue(restored.management.rosters.none { it.missing > 0 })
            assertFalse(restored.detectedEvents.first { it.id == "5" }.needsReview)
            auth.login("luis@example.com", "Prueba123")
            awaitCondition { !workflow.state.value.isPublished }
            assertEquals(2, workflow.state.value.homeScore)
        } finally { scope.cancel(); database.close() }
    }

    @Test fun versionOneDatabaseMigratesWithoutLosingTheSession() = runBlocking {
        val name = "migration-test-${UUID.randomUUID()}.db"
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { old ->
            old.execSQL("CREATE TABLE user_session (id INTEGER NOT NULL, userId TEXT, userName TEXT, email TEXT, role TEXT, selectedLeagueId TEXT, isAuthenticated INTEGER NOT NULL, onboardingCompleted INTEGER NOT NULL, PRIMARY KEY(id))")
            old.execSQL("INSERT INTO user_session VALUES(1, 'legacy-id', 'Usuario anterior', 'legacy@example.com', 'PLAYER', 'university', 1, 1)")
            old.version = 1
        }
        val migrated = Room.databaseBuilder(context, LeagueHubDatabase::class.java, name)
            .addMigrations(LeagueHubDatabase.MIGRATION_1_2, LeagueHubDatabase.MIGRATION_2_3, LeagueHubDatabase.MIGRATION_3_4, LeagueHubDatabase.MIGRATION_4_5).build()
        try {
            val session = migrated.userSessionDao().observeSession().first()!!
            assertEquals("Usuario anterior", session.userName)
            assertTrue(session.onboardingCompleted)
            assertNull(migrated.userAccountDao().findByEmail("legacy@example.com"))
            assertNull(migrated.userDataDao().read("legacy-id", "appearance"))
        } finally { migrated.close(); context.deleteDatabase(name) }
    }
}