package com.leaguehub.app.feature.organizer.validation.ocr

import androidx.lifecycle.ViewModel
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OcrReviewViewModel(
    private val repository: MatchWorkflowRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OcrReviewState(repository.state.value.ocrReview))
    val state: StateFlow<OcrReviewState> = _state.asStateFlow()

    fun onEvent(event: OcrReviewEvent) {
        when (event) {
            OcrReviewEvent.RefereeEditClicked -> {
                repository.updateReferee("Marco Salazar (revisado)")
                _state.update { it.copy(data = repository.state.value.ocrReview) }
            }
            OcrReviewEvent.ScoreEditClicked -> {
                repository.updateScore(homeScore = _state.value.data.homeScore + 1)
                _state.update { it.copy(data = repository.state.value.ocrReview) }
            }
            OcrReviewEvent.ValidateClicked -> _state.update { it.copy(isValidated = true) }
        }
    }
}
