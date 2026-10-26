package com.leaguehub.app.feature.matches.matchcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchCenterViewModel(
    matchId: String,
    workflowRepository: MatchWorkflowRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MatchCenterState())
    val state: StateFlow<MatchCenterState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            workflowRepository.state.collect { workflow ->
                _state.update { current ->
                    current.copy(
                        match = workflow.management.match.copy(
                            id = matchId,
                            homeScore = workflow.homeScore,
                            awayScore = workflow.awayScore
                        )
                    )
                }
            }
        }
    }

    fun onEvent(event: MatchCenterEvent) {
        when (event) {
            is MatchCenterEvent.TabSelected -> _state.update { it.copy(selectedTab = event.tab) }
            is MatchCenterEvent.TeamSelected -> _state.update { it.copy(selectedTeamId = event.teamId) }
        }
    }
}
