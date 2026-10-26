package com.leaguehub.app.feature.organizer.validation.ocr

data class OcrReviewState(
    val data: OcrReviewUiModel,
    val isValidated: Boolean = false
)
