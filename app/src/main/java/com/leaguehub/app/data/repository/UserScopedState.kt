package com.leaguehub.app.data.repository

import android.util.Log
import com.leaguehub.app.data.local.UserDataDao
import com.leaguehub.app.data.local.UserDataEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** One ordered queue for restores and edits. Pending edits never overwrite a restored snapshot. */
class UserScopedState<T>(
    private val sessionRepository: UserSessionRepository,
    private val dao: UserDataDao,
    scope: CoroutineScope,
    private val key: String,
    private val default: () -> T,
    private val encode: (T) -> String,
    private val decode: (String) -> T,
    private val onError: (String) -> Unit = {}
) {
    private val mutable = MutableStateFlow(default())
    val state: StateFlow<T> = mutable.asStateFlow()
    val error = MutableStateFlow<String?>(null)
    private var owner: String? = null
    private var loaded = false
    private val operations = Channel<suspend () -> Unit>(Channel.UNLIMITED)

    init {
        scope.launch {
            for (operation in operations) {
                try { operation(); error.value = null }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (failure: Exception) {
                    error.value = "No se pudieron guardar o restaurar los datos locales. Vuelve a intentarlo."
                    onError(error.value!!)
                    Log.e("LeagueHubStorage", "Error en almacenamiento local: $key", failure)
                }
            }
        }
        scope.launch {
            sessionRepository.session.map { it.userId }.distinctUntilChanged().collect { id ->
                operations.send { if (id == sessionRepository.session.value.userId) load(id) }
            }
        }
    }

    private suspend fun load(id: String?) {
        if (loaded && owner == id) return
        owner = id
        loaded = false
        mutable.value = default()
        val value = id?.let { dao.read(it, key) }?.let(decode) ?: default()
        if (sessionRepository.session.value.userId == id) {
            mutable.value = value
            loaded = true
        }
    }

    fun update(transform: (T) -> T) {
        val id = sessionRepository.session.value.userId ?: return
        operations.trySend {
            val current = dao.read(id, key)?.let(decode) ?: default()
            val updated = transform(current)
            dao.upsert(UserDataEntity(id, key, encode(updated)))
            if (id == sessionRepository.session.value.userId) {
                owner = id
                loaded = true
                mutable.value = updated
            }
        }
    }
}