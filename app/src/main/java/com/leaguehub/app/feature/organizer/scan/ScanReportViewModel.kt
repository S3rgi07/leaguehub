package com.leaguehub.app.feature.organizer.scan

import androidx.lifecycle.ViewModel
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScanReportViewModel(
    private val repository: MatchWorkflowRepository
) : ViewModel() {
    private val workflow = repository.state.value
    private val _state = MutableStateFlow(
        ScanReportState(
            match = workflow.management.match,
            status = workflow.scanStatus,
            homeScore = workflow.homeScore,
            awayScore = workflow.awayScore
        )
    )
    val state: StateFlow<ScanReportState> = _state.asStateFlow()

    fun onEvent(event: ScanReportEvent) {
        when (event) {
            is ScanReportEvent.HomeScoreChanged -> {
                repository.updateScore(homeScore = event.score)
                _state.update { it.copy(homeScore = event.score.coerceAtLeast(0)) }
            }
            is ScanReportEvent.AwayScoreChanged -> {
                repository.updateScore(awayScore = event.score)
                _state.update { it.copy(awayScore = event.score.coerceAtLeast(0)) }
            }
            ScanReportEvent.RetryClicked -> {
                repository.retryScan()
                _state.update { it.copy(status = repository.state.value.scanStatus) }
            }
            ScanReportEvent.ProcessClicked -> _state.update { it.copy(isProcessed = true) }
        }
    }
}
