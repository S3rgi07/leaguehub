package com.leaguehub.app.data.repository

import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import com.leaguehub.app.data.local.UserSessionDao
import com.leaguehub.app.data.local.UserSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserSessionRepository(
    private val sessionDao: UserSessionDao,
    private val applicationScope: CoroutineScope
) {

    private val _session = MutableStateFlow(UserSession())

    val session: StateFlow<UserSession> = _session.asStateFlow()

    private val _restoreCompleted = MutableStateFlow(false)
    val restoreCompleted: StateFlow<Boolean> = _restoreCompleted.asStateFlow()
    private val persistenceMutex = Mutex()

    init {
        applicationScope.launch {
            sessionDao.observeSession().collect { storedSession ->
                _session.value = storedSession?.toDomain() ?: UserSession()
                _restoreCompleted.value = true
            }
        }
    }

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
        persistCurrentSession()
    }

    fun selectRole(role: SessionRole) {
        _session.update {
            it.copy(role = role)
        }
        persistCurrentSession()
    }

    fun selectLeague(leagueId: String?) {
        _session.update {
            it.copy(selectedLeagueId = leagueId)
        }
        persistCurrentSession()
    }

    fun completeOnboarding() {
        _session.update {
            it.copy(onboardingCompleted = true)
        }
        persistCurrentSession()
    }

    fun logout() {
        _session.value = UserSession()
        applicationScope.launch {
            persistenceMutex.withLock {
                if (_session.value == UserSession()) sessionDao.clear()
            }
        }
    }

    private fun persistCurrentSession() {
        val snapshot = UserSessionEntity.fromDomain(_session.value)
        applicationScope.launch {
            persistenceMutex.withLock {
                if (snapshot.toDomain() == _session.value) sessionDao.upsert(snapshot)
            }
        }
    }
}
