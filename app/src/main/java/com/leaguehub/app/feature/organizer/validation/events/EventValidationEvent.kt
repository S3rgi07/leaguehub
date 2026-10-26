package com.leaguehub.app.feature.organizer.validation.events

sealed interface EventValidationEvent {
    data class EventEditClicked(val eventId: String) : EventValidationEvent
    data object AddEventClicked : EventValidationEvent
    data object CorrectDataClicked : EventValidationEvent
    data object PublishClicked : EventValidationEvent
}
