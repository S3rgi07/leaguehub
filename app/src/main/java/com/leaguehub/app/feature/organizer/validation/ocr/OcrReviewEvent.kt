package com.leaguehub.app.feature.organizer.validation.ocr

sealed interface OcrReviewEvent {
    data object RefereeEditClicked : OcrReviewEvent
    data object ScoreEditClicked : OcrReviewEvent
    data object ValidateClicked : OcrReviewEvent
}
