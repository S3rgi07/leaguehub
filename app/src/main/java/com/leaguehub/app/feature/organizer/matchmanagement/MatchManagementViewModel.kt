package com.leaguehub.app.feature.organizer.matchmanagement

import androidx.lifecycle.ViewModel
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MatchManagementViewModel(
    workflowRepository: MatchWorkflowRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MatchManagementState(data = workflowRepository.state.value.management))
    val state: StateFlow<MatchManagementState> = _state.asStateFlow()

    fun onEvent(event: MatchManagementEvent) {
        when (event) {
            is MatchManagementEvent.RosterSelected -> _state.update { current ->
                current.copy(
                    selectedRosterId = event.teamId,
                    errorMessage = null,
                    data = current.data.copy(
                        rosters = current.data.rosters.map { roster ->
                            if (roster.team.id == event.teamId) {
                                roster.copy(starters = 11, missing = 0)
                            } else roster
                        }
                    )
                )
            }
            MatchManagementEvent.StartLiveClicked -> {
                val incomplete = _state.value.data.rosters.any { it.missing > 0 }
                _state.update {
                    it.copy(errorMessage = if (incomplete) {
                        "Completa las plantillas antes de iniciar el partido."
                    } else null)
                }
            }
        }
    }
}
