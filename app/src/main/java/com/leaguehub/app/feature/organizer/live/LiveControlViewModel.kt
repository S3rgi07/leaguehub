package com.leaguehub.app.feature.organizer.live

import androidx.lifecycle.ViewModel
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LiveControlViewModel(
    private val repository: MatchWorkflowRepository
) : ViewModel() {
    private val workflow = repository.state.value
    private val _state = MutableStateFlow(
        LiveControlState(
            workflow.homeScore,
            workflow.awayScore,
            workflow.matchTime,
            workflow.liveEvents,
            workflow.isPaused
        )
    )
    val state: StateFlow<LiveControlState> = _state.asStateFlow()

    fun onEvent(event: LiveControlEvent) {
        when (event) {
            LiveControlEvent.GoalClicked -> {
                repository.addLiveEvent("Gol de Falcons", "Marcador actualizado", homeGoal = true)
                sync()
            }
            LiveControlEvent.CardClicked -> {
                repository.addLiveEvent("Tarjeta amarilla", "Evento disciplinario")
                sync()
            }
            LiveControlEvent.SubstitutionClicked -> {
                repository.addLiveEvent("Sustitución", "Cambio confirmado")
                sync()
            }
            LiveControlEvent.PauseClicked -> {
                repository.togglePaused()
                sync()
            }
            is LiveControlEvent.EventEditClicked -> _state.update { current ->
                current.copy(recentEvents = current.recentEvents.map {
                    if (it.id == event.eventId) it.copy(title = "${it.title} · editado") else it
                })
            }
            LiveControlEvent.FinishMatchClicked -> {
                repository.publish()
                _state.update { it.copy(isFinished = true, isPaused = true) }
            }
        }
    }

    private fun sync() {
        val current = repository.state.value
        _state.update {
            it.copy(
                homeScore = current.homeScore,
                awayScore = current.awayScore,
                recentEvents = current.liveEvents,
                isPaused = current.isPaused
            )
        }
    }
}
