package com.leaguehub.app.navigation

import kotlinx.serialization.Serializable

// AUTENTICACIÓN Y ONBOARDING

@Serializable
data object SplashDestination

@Serializable
data object OnboardingDestination

@Serializable
data object LoginDestination

@Serializable
data object RegisterDestination

@Serializable
data object RoleSelectionDestination

@Serializable
data object LeagueSelectionDestination

@Serializable
data object PermissionsDestination

@Serializable
data object RecoveryDestination


// JUGADOR / AFICIONADO

@Serializable
data object HomeDestination

@Serializable
data class MatchCenterDestination(
    val matchId: String
)

@Serializable
data object StandingsDestination

@Serializable
data class PlayerProfileDestination(
    val playerId: String
)

@Serializable
data object CalendarDestination

@Serializable
data class MatchDetailDestination(
    val matchId: String
)

@Serializable
data class LeagueDetailDestination(
    val leagueId: String
)

@Serializable
data class TeamDetailDestination(
    val teamId: String
)

@Serializable
data object NotificationsDestination

@Serializable
data object SettingsDestination

@Serializable
data object ExploreDestination


// ORGANIZADOR

@Serializable
data object OrganizerDashboardDestination

@Serializable
data class MatchManagementDestination(
    val matchId: String
)

@Serializable
data class ScanReportDestination(
    val matchId: String
)

@Serializable
data class OcrReviewDestination(
    val matchId: String
)

@Serializable
data class EventValidationDestination(
    val matchId: String
)

@Serializable
data class LiveControlDestination(
    val matchId: String
)

@Serializable
data class PublishedResultDestination(
    val matchId: String
)