package com.leaguehub.app

import com.leaguehub.app.data.local.UserSessionEntity
import com.leaguehub.app.data.remote.CommunityAnnouncementDto
import com.leaguehub.app.data.remote.LeagueHubApi
import com.leaguehub.app.data.repository.CommunityRepository
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DataLayerTest {
    @Test
    fun roomEntityPreservesTheCompleteSession() {
        val session = UserSession(
            userId = "user_sofia_001",
            userName = "Sofía Méndez",
            email = "sofia@leaguehub.app",
            role = SessionRole.PLAYER,
            selectedLeagueId = "university",
            isAuthenticated = true,
            onboardingCompleted = true
        )

        assertEquals(session, UserSessionEntity.fromDomain(session).toDomain())
    }

    @Test
    fun retrofitResponseIsExposedAsACommunityAnnouncement() = runBlocking {
        val api = object : LeagueHubApi {
            override suspend fun getCommunityAnnouncement() =
                CommunityAnnouncementDto(1, "liga actualizada", "contenido")
        }

        val result = CommunityRepository(api).loadAnnouncement()

        assertEquals("Liga actualizada", result.getOrThrow())
    }
}
