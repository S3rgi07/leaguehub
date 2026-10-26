package com.leaguehub.app.feature.organizer.live

sealed interface LiveControlEvent {
    data object GoalClicked : LiveControlEvent
    data object CardClicked : LiveControlEvent
    data object SubstitutionClicked : LiveControlEvent
    data object PauseClicked : LiveControlEvent
    data class EventEditClicked(val eventId: String) : LiveControlEvent
    data object FinishMatchClicked : LiveControlEvent
}
