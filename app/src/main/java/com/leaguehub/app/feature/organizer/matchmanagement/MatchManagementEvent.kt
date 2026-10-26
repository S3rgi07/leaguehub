package com.leaguehub.app.feature.organizer.matchmanagement

sealed interface MatchManagementEvent {
    data class RosterSelected(val teamId: String) : MatchManagementEvent
    data object StartLiveClicked : MatchManagementEvent
}
