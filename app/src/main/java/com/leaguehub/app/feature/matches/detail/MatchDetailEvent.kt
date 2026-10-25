package com.leaguehub.app.feature.matches.detail

sealed interface MatchDetailEvent {
    data object RefreshClicked : MatchDetailEvent
}
