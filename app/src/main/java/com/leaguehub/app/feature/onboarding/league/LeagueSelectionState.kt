package com.leaguehub.app.feature.onboarding.league

data class LeagueSelectionState(
    val searchQuery: String = "",
    val leagues: List<LeagueOptionUiModel> = emptyList(),
    val popularLeagues: List<LeagueOptionUiModel> = emptyList(),
    val selectedLeagueId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSelectionConfirmed: Boolean = false,
    val isSkipped: Boolean = false
)