package com.leaguehub.app.feature.organizer.result

import androidx.lifecycle.ViewModel
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

    fun onEvent(event: PublishedResultEvent) = when (event) {
        PublishedResultEvent.MatchCenterClicked -> Unit
        PublishedResultEvent.DashboardClicked -> Unit
    }
}
