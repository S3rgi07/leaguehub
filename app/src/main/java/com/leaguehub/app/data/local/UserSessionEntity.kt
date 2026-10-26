package com.leaguehub.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val id: Int = SINGLE_SESSION_ID,
    val userId: String?,
    val userName: String?,
    val email: String?,
    val role: String?,
    val selectedLeagueId: String?,
    val isAuthenticated: Boolean,
    val onboardingCompleted: Boolean
) {
    fun toDomain() = UserSession(
        userId = userId,
        userName = userName,
        email = email,
        role = role?.let(SessionRole::valueOf),
        selectedLeagueId = selectedLeagueId,
        isAuthenticated = isAuthenticated,
        onboardingCompleted = onboardingCompleted
    )

    companion object {
        const val SINGLE_SESSION_ID = 1

        fun fromDomain(session: UserSession) = UserSessionEntity(
            userId = session.userId,
            userName = session.userName,
            email = session.email,
            role = session.role?.name,
            selectedLeagueId = session.selectedLeagueId,
            isAuthenticated = session.isAuthenticated,
            onboardingCompleted = session.onboardingCompleted
        )
    }
}
