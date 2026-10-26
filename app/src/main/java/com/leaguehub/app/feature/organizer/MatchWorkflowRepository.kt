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

class MatchWorkflowRepository {
    private val _state = MutableStateFlow(MatchWorkflowState())
    val state: StateFlow<MatchWorkflowState> = _state.asStateFlow()

    fun updateScore(homeScore: Int? = null, awayScore: Int? = null) {
        _state.update {
            it.copy(
                homeScore = (homeScore ?: it.homeScore).coerceAtLeast(0),
                awayScore = (awayScore ?: it.awayScore).coerceAtLeast(0)
            )
        }
    }

    fun retryScan() = _state.update { it.copy(scanStatus = ScanStatus.DETECTED) }
    fun updateReferee(value: String) = _state.update { it.copy(referee = value) }

    fun addDetectedEvent() = _state.update { current ->
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
        _state.update { current ->
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

    fun togglePaused() = _state.update { it.copy(isPaused = !it.isPaused) }
    fun publish() = _state.update { it.copy(isPublished = true, isPaused = true) }
}
