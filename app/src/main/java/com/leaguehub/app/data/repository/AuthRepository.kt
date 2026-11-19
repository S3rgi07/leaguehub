package com.leaguehub.app.data.repository
import androidx.room.withTransaction
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.local.UserAccountEntity
import com.leaguehub.app.data.local.UserSessionEntity
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val database: LeagueHubDatabase, private val sessionRepository: UserSessionRepository) {
    suspend fun register(name: String, email: String, password: String) = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase(Locale.ROOT)
        val salt = PasswordHasher.newSalt()
        val account = UserAccountEntity(UUID.randomUUID().toString(), name.trim(), normalizedEmail,
            PasswordHasher.hash(password, salt), salt)
        val session = account.toSession()
        sessionRepository.replacePersistedSession(session) { database.withTransaction {
            require(database.userAccountDao().findByEmail(normalizedEmail) == null) { "Ya existe una cuenta con este correo" }
            database.userAccountDao().insert(account)
            database.userSessionDao().upsert(UserSessionEntity.fromDomain(session))
        } }
    }
    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        sessionRepository.authenticate {
            val account = database.userAccountDao().findByEmail(email.trim().lowercase(Locale.ROOT))
            require(account != null && PasswordHasher.matches(password, account.passwordSalt, account.passwordHash)) {
                "Correo o contraseña incorrectos"
            }
            account.toSession()
        }
    }
    private fun UserAccountEntity.toSession() = UserSession(
        userId = userId, userName = name, email = email, role = role?.let(SessionRole::valueOf),
        selectedLeagueId = selectedLeagueId, isAuthenticated = true, onboardingCompleted = onboardingCompleted
    )
}