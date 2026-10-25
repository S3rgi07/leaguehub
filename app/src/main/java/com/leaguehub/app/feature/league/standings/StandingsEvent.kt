package com.leaguehub.app.feature.league.standings

sealed interface StandingsEvent {
    data class ScopeChanged(val scope: StandingsScope) : StandingsEvent
}
