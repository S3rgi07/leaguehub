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
    suspend fun updateName(name: String) = withContext(Dispatchers.IO) {
        require(name.trim().length >= 3) { "Ingresa tu nombre completo" }
        sessionRepository.authenticate {
            val session = sessionRepository.session.value
            val id = requireNotNull(session.userId) { "Inicia sesión de nuevo" }
            database.userAccountDao().updateName(id, name.trim())
            session.copy(userName = name.trim())
        }
    }
    suspend fun changePassword(current: String, replacement: String) = withContext(Dispatchers.IO) {
        require(replacement.length >= 8 && replacement.any(Char::isUpperCase) && replacement.any(Char::isDigit)) { "Usa al menos 8 caracteres, una mayúscula y un número" }
        sessionRepository.authenticate {
            val session = sessionRepository.session.value
            val account = requireNotNull(session.userId?.let { database.userAccountDao().findById(it) }) { "Esta sesión antigua requiere crear una cuenta" }
            require(PasswordHasher.matches(current, account.passwordSalt, account.passwordHash)) { "La contraseña actual es incorrecta" }
            val salt = PasswordHasher.newSalt()
            database.userAccountDao().updatePassword(account.userId, PasswordHasher.hash(replacement, salt), salt)
            session
        }
    }
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
