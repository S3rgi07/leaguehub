package com.leaguehub.app.feature.organizer.validation.events

import androidx.lifecycle.ViewModel
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EventValidationViewModel(
    private val repository: MatchWorkflowRepository
) : ViewModel() {
    private val workflow = repository.state.value
    private val _state = MutableStateFlow(
        EventValidationState(workflow.detectedEvents, workflow.homeScore, workflow.awayScore)
    )
    val state: StateFlow<EventValidationState> = _state.asStateFlow()

    fun onEvent(event: EventValidationEvent) {
        when (event) {
            is EventValidationEvent.EventEditClicked -> _state.update { current ->
                current.copy(events = current.events.map {
                    if (it.id == event.eventId) it.copy(title = "${it.title} · revisado") else it
                })
            }
            EventValidationEvent.AddEventClicked -> {
                repository.addDetectedEvent()
                _state.update { it.copy(events = repository.state.value.detectedEvents) }
            }
            EventValidationEvent.CorrectDataClicked -> Unit
            EventValidationEvent.PublishClicked -> {
                repository.publish()
                _state.update { it.copy(isPublished = true) }
            }
        }
    }
}
