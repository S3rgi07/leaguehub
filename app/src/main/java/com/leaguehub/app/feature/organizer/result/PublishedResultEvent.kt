package com.leaguehub.app.feature.organizer.result

sealed interface PublishedResultEvent {
    data object MatchCenterClicked : PublishedResultEvent
    data object DashboardClicked : PublishedResultEvent
}
