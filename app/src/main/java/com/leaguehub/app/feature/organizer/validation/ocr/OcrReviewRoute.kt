package com.leaguehub.app.feature.organizer.validation.ocr

import androidx.compose.runtime.Composable

@Composable
fun OcrReviewRoute() {
    OcrReviewScreen(
        data = OcrReviewUiModel(
            homeTeam = "Falcons FC",
            awayTeam = "Titans",
            dateTime = "06 sep 2026 · 19:30",
            referee = "Marco Salazar",
            homeScore = 2,
            awayScore = 1,
            confidence = 94,
            detectedFields = 12
        )
    )
}
