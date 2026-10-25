package com.leaguehub.app.feature.onboarding.league

sealed interface LeagueSelectionEvent {

    data class SearchChanged(
        val query: String
    ) : LeagueSelectionEvent

    data class LeagueSelected(
        val leagueId: String
    ) : LeagueSelectionEvent

    data object ContinueClicked : LeagueSelectionEvent

    data object ExploreLaterClicked : LeagueSelectionEvent
}