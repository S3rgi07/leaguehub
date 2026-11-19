package com.leaguehub.app.feature.organizer

import com.leaguehub.app.feature.organizer.live.LiveEventUiModel
import com.leaguehub.app.feature.organizer.validation.events.DetectedEventUiModel

/** Persist editable values only; UI colors and LocalDate fixtures stay outside JSON. */
data class WorkflowSnapshot(
    val homeScore: Int, val awayScore: Int, val referee: String,
    val detectedEvents: List<DetectedEventUiModel>, val liveEvents: List<LiveEventUiModel>,
    val isPaused: Boolean, val isPublished: Boolean, val completedRosterIds: List<String>
) {
    fun toState() = MatchWorkflowState(homeScore = homeScore, awayScore = awayScore, referee = referee,
        detectedEvents = detectedEvents, liveEvents = liveEvents, isPaused = isPaused, isPublished = isPublished).let { state ->
            state.copy(management = state.management.copy(rosters = state.management.rosters.map {
                if (it.team.id in completedRosterIds) it.copy(starters = 11, missing = 0) else it
            }))
        }
    companion object {
        fun from(state: MatchWorkflowState) = WorkflowSnapshot(state.homeScore, state.awayScore, state.referee,
            state.detectedEvents, state.liveEvents, state.isPaused, state.isPublished, state.management.rosters.filter { it.missing == 0 }.map { it.team.id })
    }
}