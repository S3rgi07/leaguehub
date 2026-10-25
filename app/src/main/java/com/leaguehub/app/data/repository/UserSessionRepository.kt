package com.leaguehub.app.data.repository

import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserSessionRepository {

    private val _session = MutableStateFlow(UserSession())

    val session: StateFlow<UserSession> = _session.asStateFlow()

    fun login(
        userId: String,
        userName: String,
        email: String
    ) {
        _session.update { currentSession ->

            if (currentSession.userId == userId) {
                // Es el mismo usuario: conservamos su configuración.
                currentSession.copy(
                    userName = userName,
                    email = email,
                    isAuthenticated = true
                )
            } else {
                // Es otro usuario: iniciamos una sesión limpia.
                UserSession(
                    userId = userId,
                    userName = userName,
                    email = email,
                    isAuthenticated = true
                )
            }
        }
    }

    fun selectRole(role: SessionRole) {
        _session.update {
            it.copy(role = role)
        }
    }

    fun selectLeague(leagueId: String?) {
        _session.update {
            it.copy(selectedLeagueId = leagueId)
        }
    }

    fun completeOnboarding() {
        _session.update {
            it.copy(onboardingCompleted = true)
        }
    }

    fun logout() {
        _session.value = UserSession()
    }
}
