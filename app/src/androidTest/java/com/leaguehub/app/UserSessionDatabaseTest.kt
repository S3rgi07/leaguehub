package com.leaguehub.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.local.UserSessionEntity
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserSessionDatabaseTest {
    private lateinit var database: LeagueHubDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LeagueHubDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun sessionIsStoredAndRestored() = runBlocking {
        val expected = UserSession(
            userId = "user_sofia_001",
            userName = "Sofía Méndez",
            email = "sofia@leaguehub.app",
            role = SessionRole.PLAYER,
            selectedLeagueId = "university",
            isAuthenticated = true,
            onboardingCompleted = true
        )

        database.userSessionDao().upsert(UserSessionEntity.fromDomain(expected))

        assertEquals(expected, database.userSessionDao().observeSession().first()?.toDomain())
    }
}
