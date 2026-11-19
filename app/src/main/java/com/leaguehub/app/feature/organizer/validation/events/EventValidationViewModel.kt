package com.leaguehub.app.feature.organizer.validation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    init { if (repository.isPersistent) viewModelScope.launch { repository.state.collect { current ->
        _state.update { it.copy(events = current.detectedEvents, homeScore = current.homeScore, awayScore = current.awayScore, isPublished = current.isPublished) }
    } } }

    fun onEvent(event: EventValidationEvent) {
        when (event) {
            is EventValidationEvent.EventEditClicked -> {
                repository.editDetectedEvent(event.eventId)
                _state.update { it.copy(events = repository.state.value.detectedEvents) }
            }
            EventValidationEvent.AddEventClicked -> {
                repository.addDetectedEvent()
                _state.update { it.copy(events = repository.state.value.detectedEvents) }
            }
            EventValidationEvent.CorrectDataClicked -> Unit
            EventValidationEvent.PublishClicked -> {
                repository.publish()
                if (!repository.isPersistent) _state.update { it.copy(isPublished = true) }
            }
        }
    }
}
