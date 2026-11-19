package com.leaguehub.app.data.repository

import com.leaguehub.app.data.local.UserAccountDao
import com.leaguehub.app.data.local.UserSessionDao
import com.leaguehub.app.data.local.UserSessionEntity
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserSessionRepository(
    private val sessionDao: UserSessionDao,
    applicationScope: CoroutineScope,
    private val accountDao: UserAccountDao? = null,
    private val onStorageError: (String) -> Unit = {}
) {
    private val _session = MutableStateFlow(UserSession())
    val session = _session.asStateFlow()
    private val _restoreCompleted = MutableStateFlow(false)
    val restoreCompleted = _restoreCompleted.asStateFlow()
    private data class Operation(val action: suspend () -> Unit, val completion: CompletableDeferred<Unit>? = null)
    private val operations = Channel<Operation>(Channel.UNLIMITED)

    init {
        applicationScope.launch {
            try { _session.value = sessionDao.observeSession().first()?.toDomain() ?: UserSession() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { onStorageError("No se pudo restaurar la sesión local.") }
            _restoreCompleted.value = true
            for (operation in operations) {
                try { operation.action(); operation.completion?.complete(Unit) }
                catch (cancelled: CancellationException) { operation.completion?.cancel(cancelled); throw cancelled }
                catch (failure: Exception) {
                    operation.completion?.completeExceptionally(failure)
                    if (operation.completion == null) onStorageError("No se pudo guardar la sesión local. Vuelve a intentarlo.")
                }
            }
        }
    }

    private suspend fun submit(action: suspend () -> Unit) {
        val completion = CompletableDeferred<Unit>()
        operations.send(Operation(action, completion))
        completion.await()
    }

    internal suspend fun replacePersistedSession(session: UserSession, persist: suspend () -> Unit) = submit {
        persist()
        _session.value = session
    }

    internal suspend fun authenticate(resolve: suspend () -> UserSession) = submit {
        val session = resolve()
        sessionDao.upsert(UserSessionEntity.fromDomain(session))
        _session.value = session
    }

    fun selectRole(role: SessionRole) {
        _session.update { it.copy(role = role) }
        persistCurrentSession()
    }
    fun selectLeague(leagueId: String?) {
        _session.update { it.copy(selectedLeagueId = leagueId) }
        persistCurrentSession()
    }
    fun completeOnboarding() {
        _session.update { it.copy(onboardingCompleted = true) }
        persistCurrentSession()
    }
    fun logout() {
        val previous = _session.value
        _session.value = UserSession()
        operations.trySend(Operation({
            persistAccountPreferences(UserSessionEntity.fromDomain(previous))
            if (_session.value == UserSession()) sessionDao.clear()
        }))
    }
    suspend fun logoutAndWait() = submit {
        persistAccountPreferences(UserSessionEntity.fromDomain(_session.value))
        sessionDao.clear()
        _session.value = UserSession()
    }
    private suspend fun persistAccountPreferences(snapshot: UserSessionEntity) {
        snapshot.userId?.let {
            accountDao?.updatePreferences(it, snapshot.role, snapshot.selectedLeagueId, snapshot.onboardingCompleted)
        }
    }
    private fun persistCurrentSession() {
        val snapshot = UserSessionEntity.fromDomain(_session.value)
        operations.trySend(Operation({
            persistAccountPreferences(snapshot)
            if (snapshot.toDomain() == _session.value) sessionDao.upsert(snapshot)
        }))
    }
}
