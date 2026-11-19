package com.leaguehub.app.feature.organizer.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PublishedResultViewModel(
    repository: MatchWorkflowRepository
) : ViewModel() {
    private val workflow = repository.state.value
    private val match = workflow.management.match
    private val _state = MutableStateFlow(
        PublishedResultState(
            homeTeam = match.homeTeam.name,
            awayTeam = match.awayTeam.name,
            homeScore = workflow.homeScore,
            awayScore = workflow.awayScore,
            matchday = match.matchday,
            venue = match.venue
        )
    )
    val state: StateFlow<PublishedResultState> = _state.asStateFlow()

    init { if (repository.isPersistent) viewModelScope.launch { repository.state.collect { current ->
        _state.value = _state.value.copy(homeScore = current.homeScore, awayScore = current.awayScore)
    } } }

    fun onEvent(event: PublishedResultEvent) = when (event) {
        PublishedResultEvent.MatchCenterClicked -> Unit
        PublishedResultEvent.DashboardClicked -> Unit
    }
}
