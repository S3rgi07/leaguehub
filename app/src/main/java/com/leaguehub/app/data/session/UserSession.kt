
package com.leaguehub.app.data.session

enum class SessionRole {
    PLAYER,
    ORGANIZER
}

data class UserSession(
    val userId: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val role: SessionRole? = null,
    val selectedLeagueId: String? = null,
    val isAuthenticated: Boolean = false,
    val onboardingCompleted: Boolean = false
)
