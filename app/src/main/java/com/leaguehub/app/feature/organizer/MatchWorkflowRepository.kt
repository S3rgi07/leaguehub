package com.leaguehub.app.feature.organizer

import com.leaguehub.app.data.fake.falconsVsTitans
import com.leaguehub.app.data.fake.fakeMatchManagement
import com.leaguehub.app.feature.organizer.live.LiveEventUiModel
import com.leaguehub.app.feature.organizer.model.MatchManagementUiModel
import com.leaguehub.app.feature.organizer.model.ScanStatus
import com.leaguehub.app.feature.organizer.validation.events.DetectedEventType
import com.leaguehub.app.feature.organizer.validation.events.DetectedEventUiModel
import com.leaguehub.app.feature.organizer.validation.events.previewEvents
import com.leaguehub.app.feature.organizer.validation.ocr.OcrReviewUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MatchWorkflowState(
    val management: MatchManagementUiModel = fakeMatchManagement,
    val homeScore: Int = falconsVsTitans.homeScore ?: 2,
    val awayScore: Int = falconsVsTitans.awayScore ?: 1,
    val scanStatus: ScanStatus = ScanStatus.DETECTED,
    val referee: String = fakeMatchManagement.referee,
    val detectedEvents: List<DetectedEventUiModel> = previewEvents,
    val liveEvents: List<LiveEventUiModel> = listOf(
        LiveEventUiModel("1", "68'", "Gol de S. Méndez", "Falcons · 2–1"),
        LiveEventUiModel("2", "61'", "Entra J. Castillo", "Titans · Sustitución")
    ),
    val matchTime: String = "75:24",
    val isPaused: Boolean = false,
    val isPublished: Boolean = false
) {
    val ocrReview: OcrReviewUiModel
        get() = OcrReviewUiModel(
            homeTeam = management.match.homeTeam.name,
            awayTeam = management.match.awayTeam.name,
            dateTime = "${management.match.date} · ${management.match.time}",
            referee = referee,
            homeScore = homeScore,
            awayScore = awayScore,
            confidence = 94,
            detectedFields = 12
        )
}

class MatchWorkflowRepository(private val storage: com.leaguehub.app.data.repository.UserScopedState<MatchWorkflowState>? = null) {
    val isPersistent: Boolean get() = storage != null
    private val _state = MutableStateFlow(MatchWorkflowState())
    val state: StateFlow<MatchWorkflowState> = storage?.state ?: _state.asStateFlow()
    private fun change(transform: (MatchWorkflowState) -> MatchWorkflowState) {
        storage?.update(transform) ?: _state.update(transform)
    }
    fun editDetectedEvent(id: String) = change { current -> current.copy(detectedEvents = current.detectedEvents.map {
        if (it.id == id) it.copy(title = "${it.title} · revisado", needsReview = false) else it
    }) }
    fun editLiveEvent(id: String) = change { current -> current.copy(liveEvents = current.liveEvents.map {
        if (it.id == id) it.copy(title = "${it.title} · editado") else it
    }) }

    fun completeRoster(teamId: String) = change { current -> current.copy(management = current.management.copy(
        rosters = current.management.rosters.map { if (it.team.id == teamId) it.copy(starters = 11, missing = 0) else it }
    )) }

    fun updateScore(homeScore: Int? = null, awayScore: Int? = null) {
        change {
            it.copy(
                homeScore = (homeScore ?: it.homeScore).coerceAtLeast(0),
                awayScore = (awayScore ?: it.awayScore).coerceAtLeast(0)
            )
        }
    }

    fun retryScan() = change { it.copy(scanStatus = ScanStatus.DETECTED) }
    fun updateReferee(value: String) = change { it.copy(referee = value) }

    fun addDetectedEvent() = change { current ->
        val nextId = "manual_${current.detectedEvents.size + 1}"
        current.copy(
            detectedEvents = current.detectedEvents + DetectedEventUiModel(
                id = nextId,
                minute = 80,
                title = "Evento agregado",
                description = "Pendiente de detalle",
                type = DetectedEventType.SUBSTITUTION
            )
        )
    }

    fun addLiveEvent(title: String, description: String, homeGoal: Boolean = false) {
        change { current ->
            current.copy(
                homeScore = current.homeScore + if (homeGoal) 1 else 0,
                liveEvents = listOf(
                    LiveEventUiModel(
                        id = "live_${current.liveEvents.size + 1}",
                        minute = "76'",
                        title = title,
                        description = description
                    )
                ) + current.liveEvents
            )
        }
    }

    fun togglePaused() = change { it.copy(isPaused = !it.isPaused) }
    fun publish() = change { it.copy(isPublished = true, isPaused = true) }
}
